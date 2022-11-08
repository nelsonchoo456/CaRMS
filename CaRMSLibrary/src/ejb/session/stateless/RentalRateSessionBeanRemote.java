/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CategoryNotFoundException;
import util.exception.RentalRateNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Remote
public interface RentalRateSessionBeanRemote {
    
    public Long createNewRentalRate(RentalRate rentalRate, String categoryName) throws CategoryNotFoundException;
    
    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException;
    
    public List<RentalRate> viewAllRentalRates();
    
    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException;
    
    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException;
    
}
