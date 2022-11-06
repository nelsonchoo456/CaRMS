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
import entity.Car;
import entity.Employee;
import entity.Model;
import entity.RentalRate;
import java.util.List;
import java.util.Scanner;
import util.enumeration.UserRoleEnum;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.InvalidAccessRightException;
import util.exception.ModelDisabledException;
import util.exception.ModelNotFoundException;
import util.exception.RentalRateNotFoundException;

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
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.SALES_MANAGER)
        {
            if (currentEmployee.getUserRoleEnum() != UserRoleEnum.ADMINISTRATOR)
            {
             throw new InvalidAccessRightException("You don't have SALES MANAGER rights to access the Sales Management module.");   
            }
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion CARMS :: Sales Management ***\n");
            System.out.println("1: Create Rental Rate");
            System.out.println("2: View All Rental Rates");
            System.out.println("3: View Rental Rate Details");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateRentalRate();
                }
                else if(response == 2)
                {
                    doViewAllRentalRates();
                }
                else if(response == 3)
                {
                    doViewRentalRateDetails();
                }
                else if(response == 4)
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
    
    private void doCreateRentalRate() 
    {
        Scanner scanner = new Scanner(System.in);
        RentalRate newRentalRate = new RentalRate();
        
        System.out.println("*** Merlion CARMS :: Sales Management :: Create Rental Rate ***\n");
        System.out.print("Enter Category ID> ");
        Long categoryId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter Name> ");
        newRentalRate.setName(scanner.nextLine().trim());
        System.out.print("Enter Day Rate> ");
        newRentalRate.setDayRate(scanner.nextLine().trim());
        System.out.print("Enter Validity Period> ");
        newRentalRate.setValidityPeriod(scanner.nextLine().trim());
        
        try {
            Long newRentalRateId = rentalRateSessionBeanRemote.createNewRentalRate(newRentalRate, categoryId);
            System.out.println("New rental rate created successfully!: " + newRentalRate.toString() + "\n");
        } catch (CategoryNotFoundException ex) {
            System.out.println("An error has occurred while creating the new rental rate!: The category does not exist\n");
        }
    }
    
    private void doViewAllRentalRates()
    {
        System.out.println("*** Merlion CARMS :: Sales Management :: View All Rental Rates ***\n");
        Scanner scanner = new Scanner(System.in);
        
        List<RentalRate> rentalRates = rentalRateSessionBeanRemote.viewAllRentalRates();
        System.out.printf("%19s%18s%18s%18s\n", "Rental Rate ID", "Name", "Day Rate", "Validity Period");

        for(RentalRate rentalRate:rentalRates)
        {
            System.out.printf("%19s%18s%18s%18s\n", rentalRate.getRateId(), rentalRate.getName(), rentalRate.getDayRate(), rentalRate.getValidityPeriod());
        }        
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewRentalRateDetails()
    {
        System.out.println("*** Merlion CARMS :: Sales Management :: View All Rental Rates ***\n");
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Rental Rate ID> ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        
        try {
            RentalRate rentalRate = rentalRateSessionBeanRemote.retrieveRentalRateById(id);
            System.out.printf("%19s%18s%18s%18s\n", "Rental Rate ID", "Name", "Day Rate", "Validity Period");
            System.out.printf("%19s%18s%18s%18s\n", rentalRate.getRateId(), rentalRate.getName(), rentalRate.getDayRate(), rentalRate.getValidityPeriod());
            System.out.println("------------------------");
            System.out.println("1: Update Rental Rate");
            System.out.println("2: Delete Rental Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            
            if(response == 1)
            {
                doUpdateRentalRate(rentalRate);
            }
            else if(response == 2)
            {
                doDeleteRentalRate(rentalRate);
            }
        } catch (RentalRateNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doUpdateRentalRate(RentalRate rentalRate)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Integer integerInput;
        
        System.out.println("*** Merlion CARMS :: Sales Management :: Update Rental Rate ***\n");
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            rentalRate.setName(input);
        }
        
        System.out.print("Enter Day Rate (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            rentalRate.setDayRate(input);
        }
        
        System.out.print("Enter Validity Period (negative number if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            rentalRate.setValidityPeriod(input);
        }
        
        try {
            rentalRateSessionBeanRemote.updateRentalRate(rentalRate);
            System.out.println("Rental Rate updated successfully!\n");
        } catch (RentalRateNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doDeleteRentalRate(RentalRate rentalRate)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** Merlion CARMS :: Sales Management :: Delete Rental Rate ***\n");
        System.out.printf("Confirm Delete Rental Rate %s (Day Rate: %s) (Enter 'Y' to Delete)> ", rentalRate.getName(), rentalRate.getDayRate());
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            try {
                rentalRateSessionBeanRemote.deleteRentalRate(rentalRate.getRateId());
                System.out.println("Rental Rate deleted successfully!\n");
            } catch (RentalRateNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public void menuOperationsManagement() throws InvalidAccessRightException
    {
        if(currentEmployee.getUserRoleEnum()!= UserRoleEnum.OPERATIONS_MANAGER)
        {
            if(currentEmployee.getUserRoleEnum()!= UserRoleEnum.ADMINISTRATOR)
            {
             throw new InvalidAccessRightException("You don't have OPERATIONS MANAGER rights to access the Operations Management module.");   
            }
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
            System.out.println("6: View All Cars");
            System.out.println("7: View Car Details");
            System.out.println("8: Back\n");
            response = 0;
            
            while(response < 1 || response > 8)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewModel();
                }
                else if(response == 2)
                {
                    doViewAllModels();
                }
                else if(response == 3)
                {
                    doUpdateModel();
                }
                else if(response == 4)
                {
                    doDeleteModel();
                }
                else if(response == 5)
                {
                    doCreateNewCar();
                }
                else if(response == 6)
                {
                    doViewAllCars();
                }
                else if(response == 7)
                {
                    doViewCarDetails();
                }
                else if(response == 8)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 8)
            {
                break;
            }
        }
    }
    
    private void doCreateNewModel() 
    {
        Scanner scanner = new Scanner(System.in);
        Model newModel = new Model();
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Create New Model ***\n");
        System.out.print("Enter Category ID> ");
        Long categoryId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter Make and Model> ");
        newModel.setMake(scanner.nextLine().trim());
        
        try {
            Long newModelId = modelSessionBeanRemote.createNewModel(newModel, categoryId);
            System.out.println("New model created successfully!: " + newModelId.toString() + "\n");
        } catch (CategoryNotFoundException ex) {
            System.out.println("An error has occurred while creating the model!: The category does not exist\n");
        }
    }
    
    private void doViewAllModels()
    {
        System.out.println("*** Merlion CARMS :: Operations Management :: View All Models ***\n");
        Scanner scanner = new Scanner(System.in);
        
        List<Model> models = modelSessionBeanRemote.viewAllModels();
        System.out.printf("%19s%18s%18s\n", "Model ID", "Make and Model", "Is Disabled");

        for(Model model:models)
        {
            System.out.printf("%19s%18s%18s\n", model.getModelId(), model.getMake(), model.isIsDisabled());
        }        
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doUpdateModel()
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Long longInput;
        Model model;
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Update Model ***\n");
        System.out.print("Enter Model ID> ");
        longInput = scanner.nextLong();
        scanner.nextLine();
        try {
            model = modelSessionBeanRemote.retrieveModelById(longInput);
            System.out.print("Enter New Make (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                model.setMake(input);
            }
            
            try {
                modelSessionBeanRemote.updateModel(model);
                System.out.println("Model updated successfully!\n");
            } catch (ModelNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (ModelNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doDeleteModel()
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Long longInput;
        Model model;
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Delete Model ***\n");
        System.out.print("Enter Model ID> ");
        longInput = scanner.nextLong();
        scanner.nextLine();
        try {
            modelSessionBeanRemote.deleteModel(longInput);
            System.out.println("Model deleted successfully!\n");
        } catch (ModelNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doCreateNewCar() 
    {
        Scanner scanner = new Scanner(System.in);
        Car newCar = new Car();
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Create New Car ***\n");
        System.out.print("Enter Model ID> ");
        Long modelId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.print("Enter License Plate Number> ");
        newCar.setLicensePlateNumber(scanner.nextLine().trim());
        
        System.out.print("Enter Colour> ");
        newCar.setColour(scanner.nextLine().trim());
        
        System.out.print("Enter Status> ");
        newCar.setStatus(scanner.nextLine().trim());
        
        System.out.print("Enter Location> ");
        newCar.setLocation(scanner.nextLine().trim());
        
        try {
            Long newCarId = carSessionBeanRemote.createNewCar(newCar, modelId);
            System.out.println("New car created successfully!: " + newCarId.toString() + "\n");
        } catch (ModelNotFoundException ex) {
            System.out.println("An error has occurred while creating the car!: The model does not exist\n");
        } catch (ModelDisabledException ex) {
            System.out.println("An error has occurred while creating the car!: The model does not exist\n");
        }
    }
    
    private void doViewAllCars()
    {
        System.out.println("*** Merlion CARMS :: Operations Management :: View All Cars ***\n");
        Scanner scanner = new Scanner(System.in);
        
        List<Car> cars = carSessionBeanRemote.viewAllCars();
        System.out.printf("%19s%30s%18s%20s\n", "Car ID", "License Plate Number", "Colour", "Status", "Location");

        for(Car car:cars)
        {
            System.out.printf("%19s%30s%18s%20s\n", car.getCarId(), car.getLicensePlateNumber(), car.getColour(), car.getStatus(), car.getLocation());
        }        
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewCarDetails()
    {
        System.out.println("*** Merlion CARMS :: Operations Management :: View Car Details ***\n");
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Car ID> ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        
        try {
            Car car = carSessionBeanRemote.retrieveCarById(id);
            System.out.printf("%19s%30s%18s%20s\n", "Car ID", "License Plate Number", "Colour", "Status", "Location");
            System.out.printf("%19s%30s%18s%20s\n", car.getCarId(), car.getLicensePlateNumber(), car.getColour(), car.getStatus(), car.getLocation());
            System.out.println("------------------------");
            System.out.println("1: Update Car");
            System.out.println("2: Delete Car");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            
            if(response == 1)
            {
                doUpdateCar(car);
            }
            else if(response == 2)
            {
                doDeleteCar(car);
            }
        } catch (CarNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doUpdateCar(Car car)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Integer integerInput;
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Update Car ***\n");
        System.out.print("Enter License Plate Number (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            car.setLicensePlateNumber(input);
        }
        
        System.out.print("Enter Colour (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            car.setColour(input);
        }
        
        System.out.print("Enter Status (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            car.setStatus(input);
        }
        
        System.out.print("Enter Location (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            car.setLocation(input);
        }
        
        try {
            carSessionBeanRemote.updateCar(car);
            System.out.println("Car updated successfully!\n");
        } catch (CarNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doDeleteCar(Car car)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Delete Car ***\n");
        System.out.printf("Confirm Delete Car %s (Enter 'Y' to Delete)> ", car.getLicensePlateNumber());
        input = scanner.nextLine().trim();
        if(input.equals("Y"))
        {
            try {
                carSessionBeanRemote.deleteCar(car.getCarId());
                System.out.println("Car deleted successfully!\n");
            } catch (CarNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
