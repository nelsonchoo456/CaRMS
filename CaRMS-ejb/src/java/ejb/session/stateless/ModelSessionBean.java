/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Model;
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
import util.exception.ModelNotFoundException;

/**
 *
 * @author lhern
 */
@Stateless
public class ModelSessionBean implements ModelSessionBeanRemote, ModelSessionBeanLocal {

    @EJB
    private CategorySessionBeanLocal categorySessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewModel(Model model, String categoryName) throws CategoryNotFoundException, InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Model>> constraintViolations = validator.validate(model);
        
        if (constraintViolations.isEmpty()) {
            try {
                Category category = categorySessionBean.retrieveCategoryByName(categoryName);
                model.setCategory(category);
                category.getModels().add(model);
                em.persist(model);
                em.flush();            
            } catch (CategoryNotFoundException ex) {
                throw new CategoryNotFoundException(ex.getMessage());
            }

            return model.getModelId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    
    @Override
    public List<Model> viewAllModels() 
    {
        Query query = em.createQuery("SELECT m FROM Model m ORDER BY m.category, m.make ASC");
        
        return query.getResultList();
    }
    
    @Override
    public Model retrieveModelById(Long modelId) throws ModelNotFoundException
    {
        Model model = em.find(Model.class, modelId);
        if (model != null) {
            model.getCars().size();
            return model;
        } else {
            throw new ModelNotFoundException("Model " + modelId + " does not exist.");
        }
    }
    
    @Override
    public Model retrieveModelByModelNameAndMake(String makeName, String modelName) throws ModelNotFoundException
    {
        Query query = em.createQuery("SELECT m FROM Model m WHERE m.model = :inModel AND m.make = :inMake");
        query.setParameter("inModel", modelName);
        query.setParameter("inMake", makeName);
        
        try {
            return (Model)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ModelNotFoundException("Model " + makeName + " " + modelName + " does not exist.");
        }
    }
    
    @Override
    public void updateModel(Model model) throws ModelNotFoundException
    {
        if (model != null && model.getModelId() != null)
        {
            Model modelToUpdate = retrieveModelById(model.getModelId());
            
            modelToUpdate.setMake(model.getMake());
            modelToUpdate.setModel(model.getModel());
            modelToUpdate.setIsDisabled(model.isIsDisabled());
        }
        else 
        {
            throw new ModelNotFoundException("Model ID not provided for Model to be updated");
        }
    }
    
    @Override
    public void deleteModel(Long modelId) throws ModelNotFoundException
    {
        Model model = retrieveModelById(modelId);
        
        model.getCategory().getModels().remove(model);
        
        if (model.getCars().isEmpty()) {
            em.remove(model);
        } else {
            model.setIsDisabled(true);
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Model>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}