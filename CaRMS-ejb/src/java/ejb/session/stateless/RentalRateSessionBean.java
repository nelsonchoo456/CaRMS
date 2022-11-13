/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.RentalRate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
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
    public Long createNewRentalRate(RentalRate rentalRate, String categoryName) throws CategoryNotFoundException, InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<RentalRate>> constraintViolations = validator.validate(rentalRate);
        
        if (constraintViolations.isEmpty()) {
            try {
                Category category = categorySessionBean.retrieveCategoryByName(categoryName);
                rentalRate.setCategory(category);
                category.getRentalRates().add(rentalRate);
            } catch (CategoryNotFoundException ex) {
                throw new CategoryNotFoundException(ex.getMessage());
            }
            em.persist(rentalRate);
            em.flush();
            return rentalRate.getRateId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
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
        Query query = em.createQuery("SELECT rr FROM RentalRate rr ORDER BY rr.category, rr.startDateTime");
        
        return query.getResultList();
    }
    
    @Override
    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException
    {
        if (rentalRate != null && rentalRate.getRateId() != null)
        {
            RentalRate rentalRateToUpdate = retrieveRentalRateById(rentalRate.getRateId());
            
            rentalRateToUpdate.setName(rentalRate.getName());
            rentalRateToUpdate.setRatePerDay(rentalRate.getRatePerDay());
            rentalRateToUpdate.setStartDateTime(rentalRate.getStartDateTime());
            rentalRateToUpdate.setEndDateTime(rentalRate.getEndDateTime());
        }
        else 
        {
            throw new RentalRateNotFoundException("Rental Rate ID not provided for Rental Rate to be updated");
        }
    }

    @Override
    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException
    {
        try {
            RentalRate rentalRate = retrieveRentalRateById(rentalRateId);
            if (rentalRate.getIsUsed()) {
                rentalRate.setIsEnabled(false);
            } else {
                rentalRate.getCategory().getRentalRates().remove(rentalRate);
                em.remove(rentalRate);
            }
        } catch (RentalRateNotFoundException ex) {
            throw new RentalRateNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public RentalRate retrieveCheapestRentalRate(Long categoryId, Date currentDate) throws RentalRateNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM RentalRate r WHERE (r.category.categoryId = :inCarCategoryId AND r.isEnabled = TRUE) AND (r.startDateTime <= :inCurrentCheckedDate AND r.endDateTime >= :inCurrentCheckedDate) OR (r.category.categoryId = :inCarCategoryId AND r.startDateTime IS NULL AND r.endDateTime IS NULL) ORDER BY r.ratePerDay ASC");
        
        query.setParameter("inCurrentCheckedDate", currentDate);
        query.setParameter("inCarCategoryId", categoryId);
        List<RentalRate> rentalRates = query.getResultList();
        System.out.println(rentalRates);
        
        if (rentalRates.isEmpty()) {
            throw new RentalRateNotFoundException("No available rates.");
        }
        return rentalRates.get(0);
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RentalRate>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
