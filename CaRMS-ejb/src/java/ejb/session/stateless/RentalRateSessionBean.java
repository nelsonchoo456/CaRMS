/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.RentalRate;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CategoryNotFoundException;
import util.exception.RentalRateNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class RentalRateSessionBean implements RentalRateSessionBeanRemote, RentalRateSessionBeanLocal {

    @EJB
    private CategorySessionBeanLocal categorySessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewRentalRate(RentalRate rentalRate, Long categoryId) throws CategoryNotFoundException
    {
        try {
            Category category = categorySessionBean.retrieveCategoryById(categoryId);
            rentalRate.setCategory(category);
            category.getRentalRates().add(rentalRate);
            em.persist(rentalRate);
            em.flush();
        } catch (CategoryNotFoundException ex) {
            throw new CategoryNotFoundException(ex.getMessage());
        }
        return rentalRate.getRateId();
    }
    
    @Override
    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException
    {
        RentalRate rentalRate = em.find(RentalRate.class, id);
        
        if (rentalRate != null) {
            return rentalRate;
        } else {
            throw new RentalRateNotFoundException("Rental Rate " + id + " does not exist.");
        }
    }
    
    @Override
    public List<RentalRate> viewAllRentalRates() 
    {
        Query query = em.createQuery("SELECT rr FROM RentalRate rr");
        
        return query.getResultList();
    }
    
    @Override
    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException
    {
        if (rentalRate != null && rentalRate.getRateId() != null)
        {
            RentalRate rentalRateToUpdate = retrieveRentalRateById(rentalRate.getRateId());
            
            rentalRateToUpdate.setName(rentalRate.getName());
            rentalRateToUpdate.setDayRate(rentalRate.getDayRate());
            rentalRateToUpdate.setValidityPeriod(rentalRate.getValidityPeriod());
        }
        else 
        {
            throw new RentalRateNotFoundException("Rental Rate ID not provided for Rental Rate to be updated");
        }
    }
    
    
    
    @Override
    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException
    {
        RentalRate rentalRate = retrieveRentalRateById(rentalRateId);
        rentalRate.getCategory().getRentalRates().remove(rentalRate);
        rentalRate.setCategory(null);
        em.remove(rentalRate);
    }
}
