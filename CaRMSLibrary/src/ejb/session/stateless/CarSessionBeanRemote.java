/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CarNotFoundException;
import util.exception.ModelDisabledException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Remote
public interface CarSessionBeanRemote {
    
    public Long createNewCar(Car car, String modelName, String makeName, String outletName) throws ModelNotFoundException, ModelDisabledException, OutletNotFoundException;
    
    public Car retrieveCarById(Long carId) throws CarNotFoundException;
    
    public List<Car> viewAllCars();
    
    public void updateCar(Car car) throws CarNotFoundException;
    
    public void deleteCar(Long carId) throws CarNotFoundException;
    
    public Car retrieveCarByLicensePlateNumber(String license) throws CarNotFoundException;
    
}
