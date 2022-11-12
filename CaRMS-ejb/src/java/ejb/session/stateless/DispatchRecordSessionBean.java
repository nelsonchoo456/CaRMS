/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.DispatchRecord;
import entity.Employee;
import entity.RentalReservation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.exception.DispatchRecordNotFoundException;
import util.exception.DriverNotWorkingInSameOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class DispatchRecordSessionBean implements DispatchRecordSessionBeanRemote, DispatchRecordSessionBeanLocal {

    @EJB
    private CarSessionBeanLocal carSessionBean;

    @EJB
    private RentalReservationSessionBeanLocal rentalReservationSessionBean;

    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewDispatchRecord(Long reservationId, Date transitDate) throws RentalReservationNotFoundException
    {
        try {
            DispatchRecord dispatchRecord = new DispatchRecord(transitDate);
            RentalReservation rentalReservation = rentalReservationSessionBean.retrieveRentalReservationByRentalReservationId(reservationId);
            dispatchRecord.setRentalReservation(rentalReservation);
            rentalReservation.setDispatchRecord(dispatchRecord);
            em.persist(dispatchRecord);
            em.flush();
            return dispatchRecord.getDispatchRecordId();
        } catch (RentalReservationNotFoundException ex) {
            throw new RentalReservationNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public List<DispatchRecord> retrieveDispatchRecordsByOutletId(Date date, Long outletId)
    {
        date.setHours(2);
        date.setMinutes(0);
        date.setSeconds(0);
        GregorianCalendar calendar = new GregorianCalendar(date.getYear() + 1900, date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        calendar.add(Calendar.DATE, 1);
        Date nextDay = calendar.getTime();
        Query query = em.createQuery("SELECT d FROM DispatchRecord d WHERE d.rentalReservation.pickupOutlet.outletId = :inOutletId AND d.transitDate >= :inToday AND d.transitDate < :inNextDay");
        query.setParameter("inOutletId", outletId);
        query.setParameter("inToday", date);
        query.setParameter("inNextDay", nextDay);
        return query.getResultList();
    }
    
    @Override
    public DispatchRecord retrieveDispatchRecordById(Long dispatchRecordId) throws DispatchRecordNotFoundException 
    {
        DispatchRecord dispatchRecord = em.find(DispatchRecord.class, dispatchRecordId);
        
        if (dispatchRecord != null) {
            return dispatchRecord;
        } else {
            throw new DispatchRecordNotFoundException("Dispatch Record " + dispatchRecordId + " does not exist.");
        }
    }
    
    @Override
    public void assignDriver(Long driverId, Long dispatchRecordId) throws DriverNotWorkingInSameOutletException, DispatchRecordNotFoundException, EmployeeNotFoundException
    {
        try {
            Employee driver = employeeSessionBean.retrieveEmployeeById(driverId);
            DispatchRecord dispatchRecord = retrieveDispatchRecordById(dispatchRecordId);
            if(driver.getOutlet().getOutletName().equals(dispatchRecord.getRentalReservation().getPickupOutlet().getOutletName())) {
                dispatchRecord.setDriver(driver);
                driver.getDispatchRecords().add(dispatchRecord);
            } else {
                throw new DriverNotWorkingInSameOutletException("Employee is not working in the current outlet");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException(ex.getMessage());
        } catch (DispatchRecordNotFoundException ex) {
            throw new DispatchRecordNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public void updateTransitAsCompleted(Long dispatchRecordId) throws DispatchRecordNotFoundException
    {
        try {
            DispatchRecord dispatchRecord = retrieveDispatchRecordById(dispatchRecordId);
            dispatchRecord.setIsCompleted(true);
        } catch (DispatchRecordNotFoundException ex) {
            throw new DispatchRecordNotFoundException(ex.getMessage());
        }
    }
}
