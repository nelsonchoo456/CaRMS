/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCustomer(Customer customer)
    {
        em.persist(customer);
        em.flush();
        return customer.getCustomerId();
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
}
