/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Partner;
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
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCustomer(Customer customer) throws InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Customer>> constraintViolations = validator.validate(customer);
        
        if (constraintViolations.isEmpty()) {
            em.persist(customer);
            em.flush();
            return customer.getCustomerId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException
    {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :inEmail");
        query.setParameter("inEmail", email);
        try {
            return (Customer)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            throw new CustomerNotFoundException("Customer with email " + email + " does not exist.");
        }
    }
    
    @Override
    public Customer retrieveCustomerById(Long id) throws CustomerNotFoundException
    {
        Customer customer = em.find(Customer.class, id);
        
        if (customer != null) {
            return customer;
        } else {
            throw new CustomerNotFoundException("Customer " + id.toString() + " does not exist.");
        }
    }
    
    @Override
    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException
    {
        try {
            Customer customer = retrieveCustomerByEmail(email);
            
            if (customer.getPassword().equals(password))
            {
                return customer;
            }
            else
            {
                throw new InvalidLoginCredentialException("Password Incorrect!");
            }
        }
        catch (CustomerNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Email does not exist.");
        }
    }
    
    @Override
    public Long createNewCustomerWithPartner(Long partnerId, Customer newCustomer) throws PartnerNotFoundException, InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        try {
            Set<ConstraintViolation<Customer>> constraintViolations = validator.validate(newCustomer);
            
            if (constraintViolations.isEmpty()) {
                Partner partner = partnerSessionBean.retrievePartnerByPartnerId(partnerId);
                partner.getCustomers().add(newCustomer);
                newCustomer.setPartner(partner);
                em.persist(newCustomer);
                em.flush();
                return newCustomer.getCustomerId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException(ex.getMessage());
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Customer>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
