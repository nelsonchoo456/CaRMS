/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import javax.ejb.Local;
import util.exception.CategoryNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface CategorySessionBeanLocal {

    public Long createNewCategory(Category category);

    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException;
    
}