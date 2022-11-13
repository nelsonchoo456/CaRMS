/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.RentalRate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
public class CategorySessionBean implements CategorySessionBeanRemote, CategorySessionBeanLocal {

    @EJB
    private RentalRateSessionBeanLocal rentalRateSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCategory(Category category) throws InputDataValidationException {
        
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Category>> constraintViolations = validator.validate(category);
        
        if (constraintViolations.isEmpty()) {
            em.persist(category);
            em.flush();

            return category.getCategoryId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
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
    
    @Override
    public List<Category> viewAllCategories()
    {
        Query query = em.createQuery("SELECT c FROM Category c ORDER BY c.categoryId ASC");
        return query.getResultList();
    }
    
    @Override
    public BigDecimal calculateTotalRentalFee(Long categoryId, Date pickUpDateTime, Date returnDateTime) throws RentalRateNotFoundException
    {
        BigDecimal totalRentalFee = new BigDecimal(0);
        returnDateTime.setHours(pickUpDateTime.getHours());
        returnDateTime.setMinutes(pickUpDateTime.getMinutes());
        
        try {
            LocalDateTime pickUpTemporal = pickUpDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime returnTemporal = returnDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Long daysToRent = ChronoUnit.DAYS.between(pickUpTemporal, returnTemporal);
            
            GregorianCalendar transitCalendar = new GregorianCalendar(
                    pickUpDateTime.getYear() + 1900,
                    pickUpDateTime.getMonth(),
                    pickUpDateTime.getDate(),
                    pickUpDateTime.getHours(),
                    pickUpDateTime.getMinutes(),
                    pickUpDateTime.getSeconds());
            
            for (int i = 0; i < daysToRent; i++) {
                RentalRate cheapestRentalRate = rentalRateSessionBean.retrieveCheapestRentalRate(categoryId, transitCalendar.getTime());
                transitCalendar.add(Calendar.DATE, 1);
                BigDecimal dailyCheapest = cheapestRentalRate.getRatePerDay();
                totalRentalFee = totalRentalFee.add(dailyCheapest);
                cheapestRentalRate.setIsUsed(true);
                System.out.println("Rental Fee is " + dailyCheapest + " " + transitCalendar.toString());
            }
            return totalRentalFee;
        } catch (RentalRateNotFoundException ex) {
            throw new RentalRateNotFoundException(ex.getMessage());
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Category>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
