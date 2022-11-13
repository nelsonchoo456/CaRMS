/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.DispatchRecordSessionBeanRemote;
import ejb.session.stateless.EjbTimerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.RentalReservationSessionBeanRemote;
import entity.Employee;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Nelson Choo
 */
public class MainApp {
    private OutletSessionBeanRemote outletSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private ModelSessionBeanRemote modelSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    private DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote;
    private RentalReservationSessionBeanRemote rentalReservationSessionBeanRemote;
    private EjbTimerSessionBeanRemote ejbTimerSessionBeanRemote;
    
    private SalesManagementModule salesManagementModule;
    
    private Employee currentEmployee;

    public MainApp() {
    }

    public MainApp(OutletSessionBeanRemote outletSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, ModelSessionBeanRemote modelSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote, RentalReservationSessionBeanRemote rentalReservationSessionBeanRemote, EjbTimerSessionBeanRemote ejbTimerSessionBeanRemote) {
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.dispatchRecordSessionBeanRemote = dispatchRecordSessionBeanRemote;
        this.rentalReservationSessionBeanRemote = rentalReservationSessionBeanRemote;
        this.ejbTimerSessionBeanRemote = ejbTimerSessionBeanRemote;
    }
    
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Merlion Car Rental Management System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        salesManagementModule  = new SalesManagementModule(outletSessionBeanRemote, employeeSessionBeanRemote, rentalRateSessionBeanRemote, modelSessionBeanRemote, categorySessionBeanRemote, carSessionBeanRemote, dispatchRecordSessionBeanRemote, rentalReservationSessionBeanRemote, ejbTimerSessionBeanRemote, currentEmployee);
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Merlion CARMS :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentEmployee = employeeSessionBeanRemote.employeeLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    private void menuMain()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion CARMS ***\n");
            System.out.println("You are login as " + currentEmployee.getName() + " with " + currentEmployee.getUserRoleEnum().toString() + " rights\n");
            System.out.println("1: Sales Management");
            System.out.println("2: Operations Management");
            System.out.println("3: Customer Service");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try {
                        salesManagementModule.menuSalesManagement();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 2)
                {
                    try
                    {
                        salesManagementModule.menuOperationsManagement();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 3)
                {
                    try
                    {
                        salesManagementModule.menuCustomerService();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            }
        }
    }
}
