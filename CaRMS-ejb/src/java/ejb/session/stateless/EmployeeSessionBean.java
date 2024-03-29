/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Outlet;
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
import util.exception.EmployeeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @EJB
    private OutletSessionBeanLocal outletSessionBean;
    

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewEmployee(Employee employee, Long outletId) throws OutletNotFoundException, InputDataValidationException
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);
        if (constraintViolations.isEmpty()) {
            try {
                Outlet outlet = outletSessionBean.retrieveOutletById(outletId);
                employee.setOutlet(outlet);
                outlet.getEmployees().add(employee);
                em.persist(employee);
                em.flush();
            } catch (OutletNotFoundException ex) {
                throw new OutletNotFoundException(ex.getMessage());
            }
            return employee.getEmployeeId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException
    {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try 
        {
            return (Employee)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            throw new EmployeeNotFoundException("Employee Username " + username + " does not exist.");
        }
    }
    
    @Override
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Employee employee = retrieveEmployeeByUsername(username);
            
            if(employee.getPassword().equals(password))
            {                
                return employee;
            }
            else
            {
                throw new InvalidLoginCredentialException("Password Incorrect!");
            }
        }
        catch(EmployeeNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist.");
        }
    }
    
    public Employee retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException
    {
        Employee employee = em.find(Employee.class, employeeId);
        
        if (employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Emplyoee " + employeeId + " does not exist.");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
