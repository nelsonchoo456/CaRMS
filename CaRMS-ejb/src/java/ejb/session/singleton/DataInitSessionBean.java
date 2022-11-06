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
            Long outletId = outletSessionBean.createOutlet(new Outlet("Buona Vista", "0800"));
            employeeSessionBean.createNewEmployee(new Employee("Default", "Admin", UserRoleEnum.ADMINISTRATOR, "admin", "password"), outletId);  
            categorySessionBean.createNewCategory(new Category("Sedan"));
        } catch (OutletNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
