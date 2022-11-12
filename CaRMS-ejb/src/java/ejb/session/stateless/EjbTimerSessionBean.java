/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.RentalReservation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.exception.NoCarsToAllocateException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB
    private DispatchRecordSessionBeanLocal dispatchRecordSessionBean;

    @EJB
    private RentalReservationSessionBeanLocal rentalReservationSessionBean;

    @EJB
    private CarSessionBeanLocal carSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Schedule(hour = "2", minute = "0", second = "0", info = "allocateCarsToCurrentDayReservations")
    public void triggerCarAllocation()
    {
        Date date = new Date();
        allocateCarsToCurrentDayReservations(date);

    }
    
    @Override
    public void allocateCarsToCurrentDayReservations(Date date) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(date);
        System.out.println(timeStamp);
        Date start = date;
        start.setHours(2);
        start.setMinutes(0);
        start.setSeconds(0);
        GregorianCalendar calendar = new GregorianCalendar(start.getYear() + 1900, start.getMonth(), start.getDate(), start.getHours(), start.getMinutes(), start.getSeconds());
        calendar.add(Calendar.DATE, 1);
        Date end = calendar.getTime();
        System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(start));
        System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(end));
        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.startDateTime >= :inStartDate AND r.startDateTime <= :inEndDate AND r.isCancelled = FALSE");
        query.setParameter("inStartDate", start);
        query.setParameter("inEndDate", end);
        List<RentalReservation> rentalReservationsToBeAllocated = query.getResultList();
        System.out.println(rentalReservationsToBeAllocated);
        List<RentalReservation> rentalReservationsThatRequiresTransit = new ArrayList<RentalReservation>();
        
        for (RentalReservation rentalReservation : rentalReservationsToBeAllocated) {
            boolean isAllocated = false;
            if (rentalReservation.getModel() != null) {
                List<Car> cars = carSessionBean.retrieveCarsByModelId(rentalReservation.getModel().getModelId());
                // checking cars at current outlet
                for (Car car : cars) {
                    if ((car.getStatus() == CarStatusEnum.AVAILABLE && car.getRentalReservation() == null) && car.getOutlet().getOutletId().equals(rentalReservation.getPickupOutlet().getOutletId())) {
                        rentalReservation.setCar(car);
                        car.setRentalReservation(rentalReservation);
                        isAllocated = true;
                        break;
                    }
                }
                if (isAllocated) {
                    continue;
                }
                // current outlet has no cars to allocate, need cars from another outlet
                for (Car car : cars) {
                    if (car.getStatus()== CarStatusEnum.AVAILABLE && car.getRentalReservation() == null) {
                        rentalReservation.setCar(car);
                        car.setRentalReservation(rentalReservation);
                        isAllocated = true;
                        rentalReservationsThatRequiresTransit.add(rentalReservation);
                        break;
                    }
                }
                if (isAllocated) {
                    continue;
                }
                // check cars on rental but returning to current outlet
                for (Car car : cars) {
                    if ((car.getStatus()== CarStatusEnum.ON_RENTAL && car.getRentalReservation() == null) && car.getRentalReservation().getReturnOutlet().getOutletName().equals(rentalReservation.getPickupOutlet().getOutletName())) {
                        if (car.getRentalReservation().getEndDateTime().before(rentalReservation.getStartDateTime())) {
                            rentalReservation.setCar(car);
                            isAllocated = true;
                            break;
                        }
                    }
                }
                // check cars on rental but returning to a different outlet
                for (Car car : cars) {
                    if ((car.getStatus()== CarStatusEnum.ON_RENTAL) && car.getRentalReservation() == null && car.getRentalReservation().getReturnOutlet().equals(rentalReservation.getPickupOutlet())) {
                        GregorianCalendar transitCalendar = new GregorianCalendar(car.getRentalReservation().getEndDateTime().getYear() + 1900, car.getRentalReservation().getEndDateTime().getMonth(), car.getRentalReservation().getEndDateTime().getDate(), start.getHours(), start.getMinutes(), start.getSeconds());
                        transitCalendar.add(Calendar.HOUR, 2);
                        Date transitEndTime = transitCalendar.getTime();
                        if (rentalReservation.getStartDateTime().after(transitEndTime)) {
                            rentalReservation.setCar(car);
                            isAllocated = true;
                            rentalReservationsThatRequiresTransit.add(rentalReservation);
                            break;
                        }
                    }
                }
                if (isAllocated) {
                    continue;
                }
            } else { // rental reservation by car category
                List<Car> cars = carSessionBean.retrieveCarsByCategoryId(rentalReservation.getCategory().getCategoryId());
                System.out.println(cars);
                for (Car car : cars) {
                    if (car.getModel().getCategory().getCategoryName().equals(rentalReservation.getCategory().getCategoryName()) && car.getRentalReservation() == null && car.getOutlet().getOutletId().equals(rentalReservation.getPickupOutlet().getOutletId())) {
                        rentalReservation.setCar(car);
                        car.setRentalReservation(rentalReservation);
                        isAllocated = true;
                        break;
                    }
                }
                if (isAllocated) {
                    continue;
                }
                // current outlet has no cars to allocate, need cars from another outlet
                Long carCategoryId = rentalReservation.getCategory().getCategoryId();
                List<Car> carsOfSameCategory = carSessionBean.retrieveCarsByCategoryId(carCategoryId);
                for (Car car : carsOfSameCategory) {
                    if ((car.getStatus()== CarStatusEnum.AVAILABLE) && car.getRentalReservation() == null) { // already available in outlet
                        rentalReservation.setCar(car);
                        car.setRentalReservation(rentalReservation);
                        isAllocated = true;
                        rentalReservationsThatRequiresTransit.add(rentalReservation);
                        break;
                    }
                }
                if (isAllocated) {
                    continue;
                }
                // check cars on rental but returning to current outlet
                for (Car car : carsOfSameCategory) {
                    if ((car.getStatus()== CarStatusEnum.ON_RENTAL) && car.getRentalReservation().getReturnOutlet().getOutletName().equals(rentalReservation.getPickupOutlet().getOutletName())) {
                        if (car.getRentalReservation().getEndDateTime().before(rentalReservation.getStartDateTime())) {
                            rentalReservation.setCar(car);
                            isAllocated = true;
                            break;
                        }
                    }
                }
                if (isAllocated) {
                    continue;
                }
                // check cars on rental but returning to a different outlet
                for (Car car : carsOfSameCategory) {
                    if ((car.getStatus()== CarStatusEnum.ON_RENTAL) && car.getRentalReservation().getReturnOutlet().equals(rentalReservation.getPickupOutlet())) {
                        GregorianCalendar transitCalendar = new GregorianCalendar(car.getRentalReservation().getEndDateTime().getYear() + 1900, car.getRentalReservation().getEndDateTime().getMonth(), car.getRentalReservation().getEndDateTime().getDate(), car.getRentalReservation().getEndDateTime().getHours(), car.getRentalReservation().getEndDateTime().getMinutes(), car.getRentalReservation().getEndDateTime().getSeconds());
                        transitCalendar.add(Calendar.HOUR, 2);
                        Date transitEndTime = transitCalendar.getTime();
                        if (rentalReservation.getStartDateTime().after(transitEndTime)) {
                            rentalReservation.setCar(car);
                            isAllocated = true;
                            rentalReservationsThatRequiresTransit.add(rentalReservation);
                            break;
                        }
                    }
                }
                if (isAllocated) {
                    continue;
                }
            }
        }
        generateDispatchRecords(date, rentalReservationsThatRequiresTransit);
    }
    
    public void generateDispatchRecords(Date date, List<RentalReservation> rentalReservationsToBeAllocated) {
        
        try {
            for (RentalReservation rentalReservation : rentalReservationsToBeAllocated) {
                Date transitStartDate = date;
                GregorianCalendar transitCalendar = new GregorianCalendar(rentalReservation.getStartDateTime().getYear() + 1900, rentalReservation.getStartDateTime().getMonth(), rentalReservation.getStartDateTime().getDate(), rentalReservation.getStartDateTime().getHours(), rentalReservation.getStartDateTime().getMinutes(), rentalReservation.getStartDateTime().getSeconds());
                transitCalendar.add(Calendar.HOUR, -2);
                transitStartDate = transitCalendar.getTime();
                dispatchRecordSessionBean.createNewDispatchRecord(rentalReservation.getRentalReservationId(), transitStartDate);
            }
        } catch (RentalReservationNotFoundException ex) {
            System.out.println("Reservation not found.");
        }
    }
}
