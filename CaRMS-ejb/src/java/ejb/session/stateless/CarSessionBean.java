/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Model;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CarNotFoundException;
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
    public Long createNewCar(Car car, Long modelId) throws ModelNotFoundException
    {
        try {
            Model model = modelSessionBean.retrieveModelById(modelId);
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
}
