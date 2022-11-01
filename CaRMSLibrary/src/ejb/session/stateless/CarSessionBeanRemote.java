/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import javax.ejb.Remote;
import util.exception.CarNotFoundException;
import util.exception.ModelNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Remote
public interface CarSessionBeanRemote {
    
    public Long createNewCar(Car car, Long modelId) throws ModelNotFoundException;
    
    public Car retrieveCarById(Long carId) throws CarNotFoundException;
    
}
