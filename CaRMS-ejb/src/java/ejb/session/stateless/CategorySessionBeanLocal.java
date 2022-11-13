/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import java.math.BigDecimal;
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
public interface CategorySessionBeanLocal {

    public Long createNewCategory(Category category) throws InputDataValidationException;

    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException;

    public Category retrieveCategoryByName(String name) throws CategoryNotFoundException;

    public List<Category> viewAllCategories();

    public BigDecimal calculateTotalRentalFee(Long categoryId, Date pickUpDateTime, Date returnDateTime) throws RentalRateNotFoundException;
    
}
