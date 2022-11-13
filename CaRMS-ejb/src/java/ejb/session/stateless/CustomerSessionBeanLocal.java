/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Local;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long createNewCustomer(Customer customer) throws InputDataValidationException;

    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;

    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException;

    public Customer retrieveCustomerById(Long id) throws CustomerNotFoundException;
    
}
