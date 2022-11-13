/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import entity.Car;
import entity.Category;
import entity.Employee;
import entity.Model;
import entity.Outlet;
import entity.Partner;
import entity.RentalRate;
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
import util.enumeration.CarStatusEnum;
import util.enumeration.RentalRateTypeEnum;
import util.enumeration.UserRoleEnum;
import util.exception.CategoryNotFoundException;
import util.exception.EmployeeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ModelDisabledException;
import util.exception.ModelNotFoundException;
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
    private PartnerSessionBeanLocal partnerSessionBean;

    @EJB
    private RentalRateSessionBeanLocal rentalRateSessionBean;

    @EJB
    private CarSessionBeanLocal carSessionBean;

    @EJB
    private ModelSessionBeanLocal modelSessionBean;

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
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y hh:mm");
            Long outletIdA = outletSessionBean.createOutlet(new Outlet("Outlet A", null, null));
            Long outletIdB = outletSessionBean.createOutlet(new Outlet("Outlet B", null, null));
            Long outletIdC = outletSessionBean.createOutlet(new Outlet("Outlet C", inputDateFormat.parse("01/12/22 08:00"), inputDateFormat.parse("31/12/22 22:00")));
            
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
            
            modelSessionBean.createNewModel(new Model("Toyota", "Corolla"), "Standard Sedan");
            modelSessionBean.createNewModel(new Model("Honda", "Civic"), "Standard Sedan");
            modelSessionBean.createNewModel(new Model("Nissan", "Sunny"), "Standard Sedan");
            modelSessionBean.createNewModel(new Model("Mercedes", "E Class"), "Luxury Sedan");
            modelSessionBean.createNewModel(new Model("BMW", "5 Series"), "Luxury Sedan");
            modelSessionBean.createNewModel(new Model("Audi", "A6"), "Luxury Sedan");
            
            carSessionBean.createNewCar(new Car("SS00A1TC", CarStatusEnum.AVAILABLE), "Toyota", "Corolla", "Outlet A");
            carSessionBean.createNewCar(new Car("SS00A2TC", CarStatusEnum.AVAILABLE), "Toyota", "Corolla", "Outlet A");
            carSessionBean.createNewCar(new Car("SS00A3TC", CarStatusEnum.AVAILABLE), "Toyota", "Corolla", "Outlet A");
            carSessionBean.createNewCar(new Car("SS00B1HC", CarStatusEnum.AVAILABLE), "Honda", "Civic", "Outlet B");
            carSessionBean.createNewCar(new Car("SS00B2HC", CarStatusEnum.AVAILABLE), "Honda", "Civic", "Outlet B");
            carSessionBean.createNewCar(new Car("SS00B3HC", CarStatusEnum.AVAILABLE), "Honda", "Civic", "Outlet B");
            carSessionBean.createNewCar(new Car("SS00C1NS", CarStatusEnum.AVAILABLE), "Nissan", "Sunny", "Outlet C");
            carSessionBean.createNewCar(new Car("SS00C2NS", CarStatusEnum.AVAILABLE), "Nissan", "Sunny", "Outlet C");
            carSessionBean.createNewCar(new Car("SS00C3NS", CarStatusEnum.REPAIR), "Nissan", "Sunny", "Outlet C");
            carSessionBean.createNewCar(new Car("LS00A4ME", CarStatusEnum.AVAILABLE), "Mercedes", "E Class", "Outlet A");
            carSessionBean.createNewCar(new Car("LS00B4B5", CarStatusEnum.AVAILABLE), "BMW", "5 Series", "Outlet B");
            carSessionBean.createNewCar(new Car("LS00C4A6", CarStatusEnum.AVAILABLE), "Audi", "A6", "Outlet C");
            
            SimpleDateFormat inputDateTimeFormat = new SimpleDateFormat("d/M/y hh:mm");
            
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Standard Sedan - Default", new BigDecimal("100"), RentalRateTypeEnum.DEFAULT, null, null, true, false), "Standard Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Standard Sedan - Weekend Promo", new BigDecimal("80"), RentalRateTypeEnum.PROMOTION, inputDateTimeFormat.parse("09/12/2022 12:00"), inputDateTimeFormat.parse("11/12/2022 00:00"), true, false), "Standard Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Family Sedan - Default", new BigDecimal("200"), RentalRateTypeEnum.DEFAULT, null, null, true, false), "Family Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Luxury Sedan - Default", new BigDecimal("300"), RentalRateTypeEnum.DEFAULT, null, null, true, false), "Luxury Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Luxury Sedan - Monday", new BigDecimal("310"), RentalRateTypeEnum.PEAK, inputDateTimeFormat.parse("05/12/2022 00:00"), inputDateTimeFormat.parse("05/12/2022 23:59"), true, false), "Luxury Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Luxury Sedan - Tuesday", new BigDecimal("320"), RentalRateTypeEnum.PEAK, inputDateTimeFormat.parse("06/12/2022 00:00"), inputDateTimeFormat.parse("06/12/2022 23:59"), true, false), "Luxury Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Luxury Sedan - Wednesday", new BigDecimal("330"), RentalRateTypeEnum.PEAK, inputDateTimeFormat.parse("07/12/2022 00:00"), inputDateTimeFormat.parse("07/12/2022 23:59"), true, false), "Luxury Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("Luxury Sedan - Weekday Promo", new BigDecimal("250"), RentalRateTypeEnum.PROMOTION, inputDateTimeFormat.parse("07/12/2022 12:00"), inputDateTimeFormat.parse("08/12/2022 12:00"), true, false), "Luxury Sedan");
            rentalRateSessionBean.createNewRentalRate(new RentalRate("SUV and Minivan - Default", new BigDecimal("400"), RentalRateTypeEnum.DEFAULT, null, null, true, false), "SUV and Minivan");
            
            partnerSessionBean.createNewPartner(new Partner("Holiday.com", "password"));
            
        } catch (OutletNotFoundException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (CategoryNotFoundException ex) {
            ex.printStackTrace();
        } catch (ModelNotFoundException ex) {
            ex.printStackTrace();
        } catch (ModelDisabledException ex) {
            ex.printStackTrace();
        } catch (InputDataValidationException ex) {
            System.out.println("Invalid data input.");
        }
    }
}
