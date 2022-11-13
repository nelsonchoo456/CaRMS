/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRate;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.RentalRateNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface RentalRateSessionBeanLocal {

    public Long createNewRentalRate(RentalRate rentalRate, String categoryName) throws CategoryNotFoundException, InputDataValidationException;

    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException;

    public List<RentalRate> viewAllRentalRates();

    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException;

    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException;

    public RentalRate retrieveCheapestRentalRate(Long categoryId, Date currentDate) throws RentalRateNotFoundException;
    
}
