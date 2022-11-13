/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Long createNewEmployee(Employee employee, Long outletId) throws OutletNotFoundException, InputDataValidationException;

    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;

    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    public Employee retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException;
    
}
