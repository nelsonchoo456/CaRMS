/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CategoryNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class CategorySessionBean implements CategorySessionBeanRemote, CategorySessionBeanLocal {

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCategory(Category category) {
        em.persist(category);
        em.flush();
        
        return category.getCategoryId();
    }
    
    @Override
    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException {
        Category category = em.find(Category.class, id);
        
        if (category != null) {
            category.getModels().size();
            category.getRentalRates().size();
            return category;
        } else {
            throw new CategoryNotFoundException("Category " + id + " does not exist.");
        }
    }
    
    @Override
    public Category retrieveCategoryByName(String name) throws CategoryNotFoundException
    {
        Query query = em.createQuery("SELECT cat FROM Category cat WHERE cat.categoryName = :inName");
        query.setParameter("inName", name);
        
        try {
            return (Category)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CategoryNotFoundException("Category " + name + " does not exist.");
        }
    }
}
