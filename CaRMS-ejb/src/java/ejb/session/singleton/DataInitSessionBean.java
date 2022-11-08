/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import entity.Category;
import entity.Employee;
import entity.Outlet;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.UserRoleEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private CategorySessionBeanLocal categorySessionBean;

    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;
    
    
    
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PostConstruct
    public void postConstruct() 
    {
        try
        {
            employeeSessionBean.retrieveEmployeeByUsername("admin");
        }
        catch(EmployeeNotFoundException ex)
        {
            initializeData();
        }
    }
    
    private void initializeData()
    {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("hh:mm");
            Long outletIdA = outletSessionBean.createOutlet(new Outlet("Outlet A", null, null));
            Long outletIdB = outletSessionBean.createOutlet(new Outlet("Outlet B", null, null));
            Long outletIdC = outletSessionBean.createOutlet(new Outlet("Outlet C", inputDateFormat.parse("10:00"), inputDateFormat.parse("22:00")));
            
            employeeSessionBean.createNewEmployee(new Employee("Admin", UserRoleEnum.ADMINISTRATOR, "admin", "password"), outletIdA);
            
            employeeSessionBean.createNewEmployee(new Employee("Employee A1", UserRoleEnum.SALES_MANAGER, "A1", "password"), outletIdA);
            employeeSessionBean.createNewEmployee(new Employee("Employee A2", UserRoleEnum.OPERATIONS_MANAGER, "A2", "password"), outletIdA);
            employeeSessionBean.createNewEmployee(new Employee("Employee A3", UserRoleEnum.CUSTOMER_SERVICE_EXECUTIVE, "A3", "password"), outletIdA);
            employeeSessionBean.createNewEmployee(new Employee("Employee A4", UserRoleEnum.EMPLOYEE, "A4", "password"), outletIdA);
            employeeSessionBean.createNewEmployee(new Employee("Employee A5", UserRoleEnum.EMPLOYEE, "A5", "password"), outletIdA);
            
            employeeSessionBean.createNewEmployee(new Employee("Employee B1", UserRoleEnum.SALES_MANAGER, "B1", "password"), outletIdB);
            employeeSessionBean.createNewEmployee(new Employee("Employee B2", UserRoleEnum.OPERATIONS_MANAGER, "B2", "password"), outletIdB);
            employeeSessionBean.createNewEmployee(new Employee("Employee B3", UserRoleEnum.CUSTOMER_SERVICE_EXECUTIVE, "B3", "password"), outletIdB);
            
            employeeSessionBean.createNewEmployee(new Employee("Employee C1", UserRoleEnum.SALES_MANAGER, "C1", "password"), outletIdC);
            employeeSessionBean.createNewEmployee(new Employee("Employee C2", UserRoleEnum.OPERATIONS_MANAGER, "C2", "password"), outletIdC);
            employeeSessionBean.createNewEmployee(new Employee("Employee C3", UserRoleEnum.CUSTOMER_SERVICE_EXECUTIVE, "C3", "password"), outletIdC);
            
            
            categorySessionBean.createNewCategory(new Category("Standard Sedan"));
            categorySessionBean.createNewCategory(new Category("Family Sedan"));
            categorySessionBean.createNewCategory(new Category("Luxury Sedan"));
            categorySessionBean.createNewCategory(new Category("SUV and Minivan"));
            
        } catch (OutletNotFoundException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
}
