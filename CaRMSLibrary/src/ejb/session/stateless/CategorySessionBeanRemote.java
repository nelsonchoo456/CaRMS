/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import javax.ejb.Remote;
import util.exception.CategoryNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Remote
public interface CategorySessionBeanRemote {
    
    public Long createNewCategory(Category category);
    
    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException;
    
    public Category retrieveCategoryByName(String name) throws CategoryNotFoundException;
}
