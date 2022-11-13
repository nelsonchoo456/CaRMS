/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Model;
import entity.Outlet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CarNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ModelDisabledException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB
    private ModelSessionBeanLocal modelSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCar(Car car, String makeName, String modelName, String outletName) throws ModelNotFoundException, ModelDisabledException, OutletNotFoundException, InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Car>> constraintViolations = validator.validate(car);
        
        if (constraintViolations.isEmpty()) {
            try {
                Model model = modelSessionBean.retrieveModelByModelNameAndMake(makeName, modelName);

                Outlet outlet = outletSessionBean.retrieveOutletByName(outletName);
                if (model.isIsDisabled()) {
                    throw new ModelDisabledException("Model " + model.getMake() + " is disabled, unable to add new cars to this model.");
                }
                car.setModel(model);
                car.setOutlet(outlet);
                outlet.getCars().add(car);
                model.getCars().add(car);
                em.persist(car);
                em.flush();
            } catch (ModelNotFoundException ex) {
                throw new ModelNotFoundException(ex.getMessage());
            } catch (OutletNotFoundException ex) {
                throw new OutletNotFoundException(ex.getMessage());
            }
            return car.getCarId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Car retrieveCarById(Long carId) throws CarNotFoundException
    {
        Car car = em.find(Car.class, carId);
        
        if (car != null) {
            return car;
        } else {
            throw new CarNotFoundException("Car " + carId + " does not exist.");
        }
    }
    
    @Override
    public List<Car> viewAllCars()
    {
        Query query = em.createQuery("SELECT c FROM Car c ORDER BY c.model.category, c.model.make, c.licensePlateNumber ASC");
        
        return query.getResultList();
    }
    
    @Override
    public void updateCar(Car car) throws CarNotFoundException
    {
        if (car != null && car.getCarId() != null) 
        {
            Car carToUpdate = retrieveCarById(car.getCarId());
            carToUpdate.setLicensePlateNumber(car.getLicensePlateNumber());
            carToUpdate.setStatus(car.getStatus());
        }
        else {
            throw new CarNotFoundException("Car ID not provided for Car to be updated");
        }
    }
    
    @Override
    public void deleteCar(Long carId) throws CarNotFoundException
    {
        Car car = retrieveCarById(carId);
        car.getModel().getCars().remove(car);
        em.remove(car);
    }
    
    public void searchCar()
    {
        List<Car> cars = viewAllCars();
        
        List<Car> availableCars = new ArrayList<Car>();
        
    }
    
    @Override
    public Car retrieveCarByLicensePlateNumber(String license) throws CarNotFoundException
    {
        Query query = em.createQuery("SELECT c FROM Car c WHERE c.licensePlateNumber = :inLicense");
        query.setParameter("inLicense", license);
        try {
            return (Car)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            throw new CarNotFoundException("Car " + license + " does not exist.");
        }
    }
    
    @Override
    public List<Car> retrieveCarsByModelId(Long modelId) {
        Query query = em.createQuery("SELECT c FROM Car c WHERE c.model.modelId = :inModelId");
        query.setParameter("inModelId", modelId);
        return query.getResultList();
    }
    
    @Override
    public List<Car> retrieveCarsByCategoryId(Long carCategoryId) {
        Query query = em.createQuery("SELECT c FROM Car c WHERE c.model.category.categoryId = :inCategoryId");
        query.setParameter("inCategoryId", carCategoryId);
        return query.getResultList();
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Car>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
