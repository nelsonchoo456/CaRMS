/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Model;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CarNotFoundException;
import util.exception.ModelDisabledException;
import util.exception.ModelNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @EJB
    private ModelSessionBeanLocal modelSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCar(Car car, Long modelId) throws ModelNotFoundException, ModelDisabledException
    {
        try {
            Model model = modelSessionBean.retrieveModelById(modelId);
            if (model.isIsDisabled()) {
                throw new ModelDisabledException("Model " + model.getMake() + " is disabled, unable to add new cars to this model.");
            }
            car.setModel(model);
            model.getCars().add(car);
            em.persist(car);
            em.flush();
        } catch (ModelNotFoundException ex) {
            throw new ModelNotFoundException(ex.getMessage());
        }
        return car.getCarId();
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
            carToUpdate.setColour(car.getColour());
            carToUpdate.setLocation(car.getLocation());
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
}
