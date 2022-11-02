/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import entity.Employee;
import entity.RentalRate;
import java.util.Scanner;
import util.enumeration.UserRoleEnum;
import util.exception.CategoryNotFoundException;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author Nelson Choo
 */
public class SalesManagementModule {
    
    private OutletSessionBeanRemote outletSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private ModelSessionBeanRemote modelSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    
    private Employee currentEmployee;

    public SalesManagementModule() {
    }

    public SalesManagementModule(OutletSessionBeanRemote outletSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, ModelSessionBeanRemote modelSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, Employee currentEmployee) {
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSalesManagement() throws InvalidAccessRightException
    {
        if(currentEmployee.getUserRoleEnum()!= UserRoleEnum.SALES_MANAGER || currentEmployee.getUserRoleEnum()!= UserRoleEnum.ADMINISTRATOR)
        {
            throw new InvalidAccessRightException("You don't have SALES MANAGER rights to access the Sales Management module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion CARMS :: Sales Management ***\n");
            System.out.println("1: Create Rental Rate");
            System.out.println("2: View All Rental Rates");
            System.out.println("3: View Rental Rate Details");
            System.out.println("4: Update Rental Rate");
            System.out.println("5: Delete Rental Rate");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateRentalRate();
                }
                else if(response == 2)
                {
                    // doViewStaffDetails();
                }
                else if(response == 3)
                {
                    // doViewAllStaffs();
                }
                else if(response == 4)
                {
                    // doCreateNewProduct();
                }
                else if(response == 5)
                {
                    // doViewProductDetails();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    private void doCreateRentalRate() 
    {
        Scanner scanner = new Scanner(System.in);
        RentalRate newRentalRate = new RentalRate();
        
        System.out.println("*** Merlion CARMS :: Sales Management :: Create Rental Rate ***\n");
        System.out.print("Enter Category ID> ");
        Long categoryId = scanner.nextLong();
        System.out.print("Enter Name> ");
        newRentalRate.setName(scanner.nextLine().trim());
        System.out.print("Enter Day Rate> ");
        newRentalRate.setDayRate(scanner.nextLine().trim());
        System.out.print("Enter Validity Period> ");
        newRentalRate.setValidityPeriod(scanner.nextLine().trim());
        
        try {
            Long newRentalRateId = rentalRateSessionBeanRemote.createNewRentalRate(newRentalRate, categoryId);
            System.out.println("New rental rate created successfully!: " + newRentalRate + "\n");
        } catch (CategoryNotFoundException ex) {
            System.out.println("An error has occurred while creating the new rental rate!: The category does not exist\n");
        }
    }
    
    
    
    public void menuOperationsManagement() throws InvalidAccessRightException
    {
        if(currentEmployee.getUserRoleEnum()!= UserRoleEnum.OPERATIONS_MANAGER || currentEmployee.getUserRoleEnum()!= UserRoleEnum.ADMINISTRATOR)
        {
            throw new InvalidAccessRightException("You don't have OPERATIONS MANAGER rights to access the Operations Management module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion CARMS :: Sales Management ***\n");
            System.out.println("1: Create New Model");
            System.out.println("2: View All Models");
            System.out.println("3: Update Model");
            System.out.println("4: Delete Model");
            System.out.println("5: Create New Car");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    // doCreateNewStaff();
                }
                else if(response == 2)
                {
                    // doViewStaffDetails();
                }
                else if(response == 3)
                {
                    // doViewAllStaffs();
                }
                else if(response == 4)
                {
                    // doCreateNewProduct();
                }
                else if(response == 5)
                {
                    // doViewProductDetails();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
}
