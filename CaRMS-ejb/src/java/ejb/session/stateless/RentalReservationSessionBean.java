/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Model;
import entity.Outlet;
import entity.Partner;
import entity.RentalReservation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CarStatusEnum;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.PartnerNotFoundException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class RentalReservationSessionBean implements RentalReservationSessionBeanRemote, RentalReservationSessionBeanLocal {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @EJB
    private ModelSessionBeanLocal modelSessionBean;

    @EJB
    private CategorySessionBeanLocal categorySessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewRentalReservation(Long categoryId, Long modelId, Long customerId, Long pickupOutletId, Long returnOutletId, RentalReservation rentalReservation) throws CategoryNotFoundException, ModelNotFoundException, CustomerNotFoundException, OutletNotFoundException, InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<RentalReservation>> constraintViolations = validator.validate(rentalReservation);
        
        if (constraintViolations.isEmpty()) {
            try {
                Customer customer = customerSessionBean.retrieveCustomerById(customerId);
                Outlet pickupOutlet = outletSessionBean.retrieveOutletById(pickupOutletId);
                Outlet returnOutlet = outletSessionBean.retrieveOutletById(returnOutletId);

                rentalReservation.setCustomer(customer);
                rentalReservation.setPickupOutlet(pickupOutlet);
                rentalReservation.setReturnOutlet(returnOutlet);

                customer.getRentalReservations().add(rentalReservation);

                Category category;
                Model model;

                if (modelId > 0) {
                    model = modelSessionBean.retrieveModelById(modelId);
                    category = model.getCategory();
                    rentalReservation.setCategory(category);
                    rentalReservation.setModel(model);
                } else {
                    category = categorySessionBean.retrieveCategoryById(categoryId);
                    rentalReservation.setCategory(category);
                }

                em.persist(rentalReservation);
                em.flush();
                return rentalReservation.getRentalReservationId();
            } catch (CategoryNotFoundException ex) {
                throw new CategoryNotFoundException(ex.getMessage());
            } catch (ModelNotFoundException ex) {
                throw new ModelNotFoundException(ex.getMessage());
            } catch (CustomerNotFoundException ex) {
                throw new CustomerNotFoundException(ex.getMessage());
            } catch (OutletNotFoundException ex) {
                throw new OutletNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        } 
    }
    
    @Override
    public RentalReservation retrieveRentalReservationByRentalReservationId(Long rentalReservationId) throws RentalReservationNotFoundException
    {
        RentalReservation rentalReservation = em.find(RentalReservation.class, rentalReservationId);
        
        if (rentalReservation != null) {
            return rentalReservation;
        } else {
            throw new RentalReservationNotFoundException("Rental Reservation " + rentalReservationId.toString() + " not found.");
        }
    }
    
    @Override
    public List<RentalReservation> retrieveRentalReservationsByCustomer(Long customerId)
    {
        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.customer.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        
        return query.getResultList();
    }
    
    @Override
    public List<RentalReservation> retrieveAllRentalReservations() {
        Query query = em.createQuery("SELECT r FROM RentalReservation r");
        return query.getResultList();
    }
    
    @Override
    public List<RentalReservation> retrieveRentalReservationsByPickupOutlet(Long outletId)
    {
        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.isPicked = FALSE AND r.car IS NOT NULL AND r.pickupOutlet.outletId = :inOutletId ");
        query.setParameter("inOutletId", outletId);
        return query.getResultList();
    }
    
    @Override
    public List<RentalReservation> retrieveRentalReservationsByReturnOutlet(Long outletId) {
        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.isComplete = FALSE AND r.isPicked = TRUE AND r.car IS NOT NULL AND r.returnOutlet.outletId = :inOutletId");
        query.setParameter("inOutletId", outletId);
        return query.getResultList();
    }
    
    @Override
    public Boolean searchCarByCategory(Date pickUpDateTime, Date returnDateTime, Long pickupOutletId, Long returnOutletId, Long carCategoryId) throws CategoryNotFoundException
    {
        List<RentalReservation> rentalReservations = new ArrayList<RentalReservation>();
        
        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.category.categoryId = :inCategoryId AND r.startDateTime < :inPickupDate AND r.endDateTime <= :inReturnDate AND r.isCancelled = FALSE");
        
        query.setParameter("inCategoryId", carCategoryId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.category.categoryId = :inCategoryId AND r.startDateTime >= :inPickupDate AND r.endDateTime <= :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inCategoryId", carCategoryId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        System.out.println("1 - query.getResultList() : " + query.getResultList());
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.category.categoryId = :inCategoryId AND r.startDateTime >= :inPickupDate AND r.endDateTime > :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inCategoryId", carCategoryId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        System.out.println("2 - query.getResultList() : " + query.getResultList());
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.category.categoryId = :inCategoryId AND r.startDateTime <= :inPickupDate AND r.endDateTime >= :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inCategoryId", carCategoryId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        System.out.println("3 - query.getResultList() : " + query.getResultList());
        
        GregorianCalendar calendar = new GregorianCalendar(pickUpDateTime.getYear() + 1900,
                pickUpDateTime.getMonth(), pickUpDateTime.getDate(), pickUpDateTime.getHours(),
                pickUpDateTime.getMinutes(), pickUpDateTime.getSeconds());
        calendar.add(Calendar.HOUR, -2);
        Date transitDate = calendar.getTime();
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.category.categoryId = :inCategoryId AND r.startDateTime < :inPickupDate AND r.endDateTime > :inTransitDate AND r.returnOutlet.outletId <> :inPickupOutletId AND r.isCancelled = FALSE");
        query.setParameter("inCategoryId", carCategoryId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inTransitDate", transitDate);
        query.setParameter("inPickupOutletId", pickupOutletId);
        rentalReservations.addAll(query.getResultList());
        System.out.println("4 - query.getResultList() : " + query.getResultList());
        
        try {
            Category category = categorySessionBean.retrieveCategoryById(carCategoryId);
            List<Car> cars = new ArrayList<Car>();
            
            for (Model model: category.getModels()) {
                cars.addAll(model.getCars());
            }
            
            System.out.println("cars : " + cars.size());
            System.out.println("rentalReservations.size() : " + rentalReservations.size());
            
            return cars.size() > rentalReservations.size();
        } catch (CategoryNotFoundException ex) {
            throw new CategoryNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public Boolean searchCarByModel(Date pickUpDateTime, Date returnDateTime, Long pickupOutletId, Long returnOutletId, Long modelId) throws ModelNotFoundException
    {
        List<RentalReservation> rentalReservations = new ArrayList<RentalReservation>();

        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.model.modelId = :inModelId AND r.startDateTime < :inPickupDate AND r.endDateTime <= :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inModelId", modelId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.model.modelId = :inModelId AND r.startDateTime >= :inPickupDate AND r.endDateTime <= :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inModelId", modelId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.model.modelId = :inModelId AND r.startDateTime >= :inPickupDate AND r.endDateTime > :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inModelId", modelId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.model.modelId = :inModelId AND r.startDateTime <= :inPickupDate AND r.endDateTime >= :inReturnDate AND r.isCancelled = FALSE");
        query.setParameter("inModelId", modelId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inReturnDate", returnDateTime);
        rentalReservations.addAll(query.getResultList());
        
        GregorianCalendar calendar = new GregorianCalendar(pickUpDateTime.getYear() + 1900,
                pickUpDateTime.getMonth(), pickUpDateTime.getDate(), pickUpDateTime.getHours(),
                pickUpDateTime.getMinutes(), pickUpDateTime.getSeconds());
        calendar.add(Calendar.HOUR, -2);
        Date transitDate = calendar.getTime();
        
        query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.model.modelId = :inModelId AND r.startDateTime < :inPickupDate AND r.endDateTime > :inTransitDate AND r.returnOutlet.outletId <> :inPickupOutletId AND r.isCancelled = FALSE");
        query.setParameter("inModelId", modelId);
        query.setParameter("inPickupDate", pickUpDateTime);
        query.setParameter("inTransitDate", transitDate);
        query.setParameter("inPickupOutletId", pickupOutletId);
        rentalReservations.addAll(query.getResultList());
        
        try {
            Model model = modelSessionBean.retrieveModelById(modelId);
            return model.getCars().size() > rentalReservations.size();
        } catch (ModelNotFoundException ex) {
            throw new ModelNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public BigDecimal cancelReservation(Long rentalReservationId) throws RentalReservationNotFoundException
    {
        try {
            RentalReservation rentalReservation = retrieveRentalReservationByRentalReservationId(rentalReservationId);
            rentalReservation.setIsCancelled(true);
            LocalDateTime startDateTemporal = rentalReservation.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime today = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Long noOfDaysToStartReservation = ChronoUnit.DAYS.between(today, startDateTemporal);
            BigDecimal price = rentalReservation.getPrice();
            BigDecimal penalty = null;
            
            if (noOfDaysToStartReservation >= 14) {
                penalty = new BigDecimal(0);
            } else if (noOfDaysToStartReservation < 14 && noOfDaysToStartReservation >= 7) {
                penalty = price.multiply(new BigDecimal(0.2));
            } else if (noOfDaysToStartReservation < 7 && noOfDaysToStartReservation >= 3) {
                penalty = price.multiply(new BigDecimal(0.5));
            } else if (noOfDaysToStartReservation < 3) {
                penalty = price.multiply(new BigDecimal(0.7));
            }
            
            return penalty;
            
        } catch (RentalReservationNotFoundException ex) {
            throw new RentalReservationNotFoundException("Rental Reservation of ID: " + rentalReservationId + " not found!");
        }
    }
    
    @Override
    public void pickupCar(Long reservationId) throws RentalReservationNotFoundException
    {
        try {
            RentalReservation rentalReservation = retrieveRentalReservationByRentalReservationId(reservationId);
            Car car = rentalReservation.getCar();
            car.setStatus(CarStatusEnum.ON_RENTAL);
            car.setOutlet(null);
            car.setRentalReservation(rentalReservation);
            rentalReservation.setPaid(true);
            rentalReservation.setIsPicked(true);
            rentalReservation.getPickupOutlet().getCars().remove(car);
        } catch (RentalReservationNotFoundException ex) {
            throw new RentalReservationNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public void returnCar(Long reservationId) throws RentalReservationNotFoundException
    {
        try {
            RentalReservation rentalReservation = retrieveRentalReservationByRentalReservationId(reservationId);
            Outlet returnOutlet = rentalReservation.getReturnOutlet();
            Car car = rentalReservation.getCar();
            car.setStatus(CarStatusEnum.AVAILABLE);
            car.setOutlet(returnOutlet);
            car.setRentalReservation(null);
            returnOutlet.getCars().add(car);
            rentalReservation.setIsComplete(Boolean.TRUE);
        } catch (RentalReservationNotFoundException ex) {
            throw new RentalReservationNotFoundException("Rental Reservation ID: " + reservationId + "not found!");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RentalReservation>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
    @Override
    public Long createNewPartnerRentalReservation(Long carCategoryId, Long partnerId, Long modelId, Long customerId, Long pickupOutletId, Long returnOutletId, RentalReservation newRentalReservation) throws OutletNotFoundException, CustomerNotFoundException, InputDataValidationException, CategoryNotFoundException, ModelNotFoundException, PartnerNotFoundException {
        try {
            
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<RentalReservation>> constraintViolations = validator.validate(newRentalReservation);

            if (constraintViolations.isEmpty()) {
                Customer customer = customerSessionBean.retrieveCustomerById(customerId);
                Partner partner = partnerSessionBean.retrievePartnerByPartnerId(partnerId);
                Outlet pickupOutlet = outletSessionBean.retrieveOutletById(pickupOutletId);
                Outlet returnOutlet = outletSessionBean.retrieveOutletById(returnOutletId);
                newRentalReservation.setCustomer(customer);
                newRentalReservation.setPartner(partner);
                newRentalReservation.setPickupOutlet(pickupOutlet);
                newRentalReservation.setReturnOutlet(returnOutlet);
                customer.getRentalReservations().add(newRentalReservation);
                Category carCategory = null;
                Model model = null;
                if (carCategoryId > 0) {
                    carCategory = categorySessionBean.retrieveCategoryById(carCategoryId);
                    newRentalReservation.setCategory(carCategory);
                } else {
                    model = modelSessionBean.retrieveModelById(modelId);
                    newRentalReservation.setModel(model);
                }
                em.persist(newRentalReservation);
                em.flush();
                return newRentalReservation.getRentalReservationId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        
        } catch (OutletNotFoundException ex) {
            throw new OutletNotFoundException("Outlet IDs: " + pickupOutletId + " and " + returnOutletId + " either or both does not exist!");
        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException("Customer ID: " + customerId + " does not exist!");
        } catch (CategoryNotFoundException ex) {
            throw new CategoryNotFoundException("Car Category ID: " + carCategoryId + " does not exist!");
        } catch (ModelNotFoundException ex) {
            throw new ModelNotFoundException("Model ID: " + modelId + " does not exist!");
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner ID: " + partnerId + " does not exist!");
        }
    }
    
    @Override
    public List<RentalReservation> retrievePartnerRentalReservations(Long partnerId) {
        Query query = em.createQuery("SELECT r FROM RentalReservation r WHERE r.partner.partnerId = :inPartnerId");
        query.setParameter("inPartnerId", partnerId);
        return query.getResultList();
    }
}
