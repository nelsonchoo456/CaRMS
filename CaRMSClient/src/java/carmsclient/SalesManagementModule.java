/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.DispatchRecordSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.RentalReservationSessionBeanRemote;
import entity.Car;
import entity.DispatchRecord;
import entity.Employee;
import entity.Model;
import entity.RentalRate;
import entity.RentalReservation;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.enumeration.CarStatusEnum;
import util.enumeration.RentalRateTypeEnum;
import util.enumeration.UserRoleEnum;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.DispatchRecordNotFoundException;
import util.exception.DriverNotWorkingInSameOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidAccessRightException;
import util.exception.ModelDisabledException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.RentalRateNotFoundException;
import util.exception.RentalReservationNotFoundException;
import util.exception.UnpaidRentalReservationException;

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
    private DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote;
    private RentalReservationSessionBeanRemote rentalReservationSessionBeanRemote;
    
    private Employee currentEmployee;

    public SalesManagementModule() {
    }

    public SalesManagementModule(OutletSessionBeanRemote outletSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, ModelSessionBeanRemote modelSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote, RentalReservationSessionBeanRemote rentalReservationSessionBeanRemote, Employee currentEmployee) {
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.dispatchRecordSessionBeanRemote = dispatchRecordSessionBeanRemote;
        this.currentEmployee = currentEmployee;
        this.rentalReservationSessionBeanRemote = rentalReservationSessionBeanRemote;
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
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y hh:mm");
            String input;
            String category;
            RentalRate newRentalRate = new RentalRate();

            System.out.println("*** Merlion CARMS :: Sales Management :: Create Rental Rate ***\n");
            System.out.print("Enter Category Name> ");
            category = scanner.nextLine().trim();
            System.out.print("Enter Rental Rate Name> ");
            newRentalRate.setName(scanner.nextLine().trim());
            System.out.print("Enter Day Rate> ");
            newRentalRate.setRatePerDay(new BigDecimal(scanner.nextLine().trim()));
            System.out.print("Enter Start Date/Time (dd/mm/yyyy hh:mm) (Enter null if always valid)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                if (input.equals("null")) {
                    newRentalRate.setStartDateTime(null);
                } else {
                    newRentalRate.setStartDateTime(inputDateFormat.parse(input));   
                }
            }
            System.out.print("Enter End Date/Time (dd/mm/yyyy hh:mm) (Enter null if always valid)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                if (input.equals("null")) {
                    newRentalRate.setEndDateTime(null);
                } else {
                    newRentalRate.setEndDateTime(inputDateFormat.parse(input));
                }
            }
            
            System.out.print("Enter Rental Rate Type (1 for Default, 2 for Promotion, 3 for Peak)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                int intInput = Integer.parseInt(input);

                if (intInput == 1) {
                    newRentalRate.setRentalRateType(RentalRateTypeEnum.DEFAULT);
                } else if (intInput == 2) {
                    newRentalRate.setRentalRateType(RentalRateTypeEnum.PROMOTION);
                } else if (intInput == 3) {
                    newRentalRate.setRentalRateType(RentalRateTypeEnum.PEAK);
                } else {
                    System.out.println("Invalid Option.");
                    return;
                }
            }

            try {
                Long newRentalRateId = rentalRateSessionBeanRemote.createNewRentalRate(newRentalRate, category);
                System.out.println("New rental rate created successfully!: " + newRentalRateId.toString() + "\n");
            } catch (CategoryNotFoundException ex) {
                System.out.println("An error has occurred while creating the new rental rate!: The category does not exist\n");
            }  
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }
    
    private void doViewAllRentalRates()
    {
        System.out.println("*** Merlion CARMS :: Sales Management :: View All Rental Rates ***\n");
        Scanner scanner = new Scanner(System.in);
        
        List<RentalRate> rentalRates = rentalRateSessionBeanRemote.viewAllRentalRates();
        System.out.printf("%19s%35s%35s%35s%35s%35s\n", "Rental Rate ID", "Name", "Rental Rate Type", "Rate per Day", "Start Date", "End Date");

        for(RentalRate rentalRate:rentalRates)
        {
            System.out.printf("%19s%35s%35s%35s%35s%35s\n", rentalRate.getRateId(), rentalRate.getName(), rentalRate.getRentalRateType(), rentalRate.getRatePerDay(), rentalRate.getStartDateTime(), rentalRate.getEndDateTime());
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
            System.out.printf("%19s%35s%35s%35s%35s%35s\n", "Rental Rate ID", "Name", "Rental Rate Type", "Rate per Day", "Start Date", "End Date");
            System.out.printf("%19s%35s%35s%35s%35s%35s\n", rentalRate.getRateId(), rentalRate.getName(), rentalRate.getRentalRateType(), rentalRate.getRatePerDay(), rentalRate.getStartDateTime(), rentalRate.getEndDateTime());
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
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y hh:mm");
        
        try {
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
                rentalRate.setRatePerDay(new BigDecimal(input));
            }

            System.out.print("Enter Start Date/Time (dd/mm/yyyy hh:mm) (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                rentalRate.setStartDateTime(inputDateFormat.parse(input));
            }
            
            System.out.print("Enter End Date/Time (dd/mm/yyyy hh:mm) (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                rentalRate.setEndDateTime(inputDateFormat.parse(input));
            }
            
            System.out.print("Enter Rental Rate Type (1 for Default, 2 for Promotion, 3 for Peak)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                int intInput = Integer.parseInt(input);

                if (intInput == 1) {
                    rentalRate.setRentalRateType(RentalRateTypeEnum.DEFAULT);
                } else if (intInput == 2) {
                    rentalRate.setRentalRateType(RentalRateTypeEnum.PROMOTION);
                } else if (intInput == 3) {
                    rentalRate.setRentalRateType(RentalRateTypeEnum.PEAK);
                } else {
                    System.out.println("Invalid Option.");
                    return;
                }
            }

            try {
                rentalRateSessionBeanRemote.updateRentalRate(rentalRate);
                System.out.println("Rental Rate updated successfully!\n");
            } catch (RentalRateNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doDeleteRentalRate(RentalRate rentalRate)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** Merlion CARMS :: Sales Management :: Delete Rental Rate ***\n");
        System.out.printf("Confirm Delete Rental Rate %s (Day Rate: %s) (Enter 'Y' to Delete)> ", rentalRate.getName(), rentalRate.getRatePerDay());
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
            System.out.println("8: View Transit Driver Dispatch Records for Current Day Reservations");
            System.out.println("9: Assign Transit Driver");
            System.out.println("10: Update Transit As Completed");
            System.out.println("11: Back\n");
            response = 0;
            
            while(response < 1 || response > 11)
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
                    doViewTransitDriverDispatchRecords();
                }
                else if(response == 9)
                {
                    doAssignTransitDriver();
                }
                else if(response == 10)
                {
                    doUpdateTransitAsCompleted();
                }
                else if(response == 11)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 11)
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
        System.out.print("Enter Category Name> ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter Make> ");
        newModel.setMake(scanner.nextLine().trim());
        System.out.print("Enter Model> ");
        newModel.setModel(scanner.nextLine().trim());
        
        try {
            Long newModelId = modelSessionBeanRemote.createNewModel(newModel, categoryName);
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
        System.out.printf("%19s%18s%18s%18s\n", "Model ID", "Make", "Model", "Is Disabled");

        for(Model model:models)
        {
            System.out.printf("%19s%18s%18s\n", model.getModelId(), model.getMake(), model.getModel(), model.isIsDisabled());
        }        
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doUpdateModel()
    {
        Scanner scanner = new Scanner(System.in);
        String input;
        Model model;
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Update Model ***\n");
        System.out.print("Enter Make> ");
        String makeName = scanner.nextLine().trim();
        System.out.print("Enter Model> ");
        String modelName = scanner.nextLine().trim();
        try {
            model = modelSessionBeanRemote.retrieveModelByModelNameAndMake(makeName, modelName);
            System.out.print("Enter New Model (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                model.setModel(input);
            }
            
            System.out.print("Enter New Make (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                model.setMake(input);
            }
            
            System.out.print("Set as disabled (Enter Y for disabled, N for not disabled)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                if (input.equals("Y")) {
                    model.setIsDisabled(true);
                } else if (input.equals("N")) {
                    model.setIsDisabled(false);
                }
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
        String input;
        Car newCar = new Car();
        
        System.out.println("*** Merlion CARMS :: Operations Management :: Create New Car ***\n");
        System.out.print("Enter Make Name> ");
        String makeName = scanner.nextLine().trim();
        
        System.out.print("Enter Model Name> ");
        String modelName = scanner.nextLine().trim();
        
        System.out.print("Enter Outlet> ");
        String outletName = scanner.nextLine().trim();
        
        System.out.print("Enter License Plate Number> ");
        newCar.setLicensePlateNumber(scanner.nextLine().trim());
        
        System.out.print("Enter Status (1 for Available, 2 for On Rental, 3 for Repair)> ");
        input = scanner.nextLine().trim();
        
        if (input.equals("1")) {
            newCar.setStatus(CarStatusEnum.AVAILABLE);
        } else if (input.equals("2")) {
            newCar.setStatus(CarStatusEnum.ON_RENTAL);
        } else if (input.equals("3")) {
            newCar.setStatus(CarStatusEnum.REPAIR);
        } else {
            System.out.println("Invalid Status.");
            return;
        }
        
        try {
            Long newCarId = carSessionBeanRemote.createNewCar(newCar, makeName, modelName, outletName);
            System.out.println("New car created successfully!: " + newCarId.toString() + "\n");
        } catch (ModelNotFoundException ex) {
            System.out.println("An error has occurred while creating the car!: The model does not exist\n");
        } catch (ModelDisabledException ex) {
            System.out.println("An error has occurred while creating the car!: The model does not exist\n");
        } catch (OutletNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doViewAllCars()
    {
        System.out.println("*** Merlion CARMS :: Operations Management :: View All Cars ***\n");
        Scanner scanner = new Scanner(System.in);
        
        List<Car> cars = carSessionBeanRemote.viewAllCars();
        System.out.printf("%19s%30s%18s%20s%20s%20s\n", "Car ID", "License Plate Number", "Make", "Model", "Status", "Outlet");

        for(Car car:cars)
        {
            System.out.printf("%19s%30s%18s%20s%20s%20s\n", car.getCarId(), car.getLicensePlateNumber(), car.getModel().getMake(), car.getModel().getModel(), car.getStatus(), car.getOutlet().getOutletName());
        }        
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewCarDetails()
    {
        System.out.println("*** Merlion CARMS :: Operations Management :: View Car Details ***\n");
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Car License Plate Number> ");
        String license = scanner.nextLine().trim();
        
        try {
            Car car = carSessionBeanRemote.retrieveCarByLicensePlateNumber(license);
            System.out.printf("%19s%30s%18s%20s%20s%20s\n", "Car ID", "License Plate Number", "Make", "Model", "Status", "Outlet");
            System.out.printf("%19s%30s%18s%20s%20s%20s\n", car.getCarId(), car.getLicensePlateNumber(), car.getModel().getMake(), car.getModel().getModel(), car.getStatus(), car.getOutlet().getOutletName());
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
        
        System.out.print("Enter Status (1 for Available, 2 for On Rental, 3 for Repair) (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            if (input.equals("1")) {
                car.setStatus(CarStatusEnum.AVAILABLE);
            } else if (input.equals("2")) {
                car.setStatus(CarStatusEnum.ON_RENTAL);
            } else if (input.equals("3")) {
                car.setStatus(CarStatusEnum.REPAIR);
            } else {
                System.out.println("Invalid Status.");
                return;
            }
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
    
    private void doViewTransitDriverDispatchRecords()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Merlion CARMS :: Operations Management :: View Transit Driver Dispatch Records for Current Day Reservations***\n");
        System.out.print("Enter Date (DD/MM/YYYY) > ");
        String inputDate = scanner.nextLine().trim();
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = inputFormat.parse(inputDate);
            
            System.out.println("Dispatch records for " + currentEmployee.getOutlet().getOutletName() + " on " + inputDate + "\n");
            List<DispatchRecord> dispatchRecords = dispatchRecordSessionBeanRemote.retrieveDispatchRecordsByOutletId(date, currentEmployee.getOutlet().getOutletId());
            System.out.printf("%12s%32s%32s%20s%20s\n", "Record ID", "Destination Outlet", "Driver", "Status", "Transit Time");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (DispatchRecord dispatchRecord : dispatchRecords) {
                String isCompleted = "Not Completed";
                if (dispatchRecord.getIsCompleted()) {
                    isCompleted = "Completed";
                }
                String dispatchDriverName = "Unassigned";
                if (dispatchRecord.getDriver() != null) {
                    dispatchDriverName = dispatchRecord.getDriver().getName();
                }
                
                String transitDate = sdf.format(dispatchRecord.getTransitDate());
                System.out.printf("%12s%32s%32s%20s%20s\n", dispatchRecord.getDispatchRecordId(), dispatchRecord.getRentalReservation().getPickupOutlet().getOutletName(), dispatchDriverName, isCompleted, transitDate);
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!");
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doAssignTransitDriver()
    {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("*** Merlion CARMS :: Operations Management :: Assign Transit Driver***\n");
            System.out.print("Enter Date (DD/MM/YYYY) > ");
            String inputDate = scanner.nextLine().trim();
            System.out.println("Dispatch records for " + currentEmployee.getOutlet().getOutletName() + " on " + inputDate + "\n");
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = inputFormat.parse(inputDate);
            List<DispatchRecord> dispatchRecords = dispatchRecordSessionBeanRemote.retrieveDispatchRecordsByOutletId(date, currentEmployee.getOutlet().getOutletId());
            if (dispatchRecords.isEmpty()) {
                System.out.println("No dispatch records to be assigned!");
            } else {
                System.out.printf("%12s%32s%32s%20s%20s\n", "Record ID", "Destination Outlet", "Driver", "Status", "Transit Time");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (DispatchRecord dispatchRecord : dispatchRecords) {
                    String isCompleted = "Not Completed";
                    if (dispatchRecord.getIsCompleted()) {
                        isCompleted = "Completed";
                    }
                    String dispatchDriverName = "Unassigned";
                    if (dispatchRecord.getDriver() != null) {
                        dispatchDriverName = dispatchRecord.getDriver().getName();
                    }
                    String transitDate = sdf.format(dispatchRecord.getTransitDate());
                    System.out.printf("%12s%32s%32s%20s%20s\n",
                            dispatchRecord.getDispatchRecordId(), dispatchRecord.getRentalReservation().getPickupOutlet().getOutletName(), dispatchDriverName, isCompleted, transitDate);
                }
                System.out.print("Enter Transit Driver Dispatch Record ID> ");
                Long dispatchRecordId = scanner.nextLong();
                System.out.print("Enter Dispatch Driver ID> ");
                Long dispatchDriverId = scanner.nextLong();
                scanner.nextLine();
                dispatchRecordSessionBeanRemote.assignDriver(dispatchDriverId, dispatchRecordId);
                System.out.println("Succesfully assigned transit driver " + dispatchDriverId + " to a dispatch record " + dispatchRecordId);
            }
        } catch (DriverNotWorkingInSameOutletException ex) {
            System.out.println("Driver is not working in the same outlet as the car is currently at!");
        } catch (EmployeeNotFoundException ex) {
            System.out.println("Employee not found!");
        } catch (DispatchRecordNotFoundException ex) {
            System.out.println("Transit driver dispatch record not found!");
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format");
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doUpdateTransitAsCompleted() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("*** Merlion CARMS :: Operations Management :: Update Transit As Completed***\n");
            System.out.print("Enter Date (DD/MM/YYYY) > ");
            String inputDate = scanner.nextLine().trim();
            System.out.println("Dispatch records for " + currentEmployee.getOutlet().getOutletName() + " on " + inputDate + "\n");
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = inputFormat.parse(inputDate);
            List<DispatchRecord> dispatchRecords = dispatchRecordSessionBeanRemote.retrieveDispatchRecordsByOutletId(date, currentEmployee.getOutlet().getOutletId());
            if (dispatchRecords.isEmpty()) {
                System.out.println("No dispatch records to be completed!");
            } else {
                System.out.printf("%12s%32s%32s%20s%20s\n", "Record ID", "Destination Outlet", "Driver", "Status", "Transit Time");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (DispatchRecord dispatchRecord : dispatchRecords) {
                    String isCompleted = "Not Completed";
                    if (dispatchRecord.getIsCompleted()) {
                        isCompleted = "Completed";
                    }
                    String dispatchDriverName = "Unassigned";
                    if (dispatchRecord.getDriver()!= null) {
                        dispatchDriverName = dispatchRecord.getDriver().getName();
                    }
                    String transitDate = sdf.format(dispatchRecord.getTransitDate());
                    System.out.printf("%12s%32s%32s%20s%20s\n", dispatchRecord.getDispatchRecordId(), dispatchRecord.getRentalReservation().getPickupOutlet().getOutletName(), dispatchDriverName, isCompleted, transitDate);
                }
                System.out.print("Enter Transit Dispatch Record ID> ");
                Long dispatchRecordId = scanner.nextLong();
                scanner.nextLine();
                dispatchRecordSessionBeanRemote.updateTransitAsCompleted(dispatchRecordId);
                System.out.println("Successfully updated transit record id: " + dispatchRecordId + " as completed!");
            }
        } catch (DispatchRecordNotFoundException ex) {
            System.out.println("Transit driver dispatch record not found!");
        } catch (ParseException ex) {
            System.out.println("Invalid date format!");
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    public void menuCustomerService() throws InvalidAccessRightException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.CUSTOMER_SERVICE_EXECUTIVE)
        {
            if (currentEmployee.getUserRoleEnum() != UserRoleEnum.ADMINISTRATOR)
            {
             throw new InvalidAccessRightException("You don't have CUSTOMER SERVICE EXECUTIVE rights to access the Sales Management module.");   
            }
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion CARMS :: Customer Service ***\n");
            System.out.println("1: Pickup Car");
            System.out.println("2: Return Car");
            System.out.println("3: Back \n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doPickupCar();
                }
                else if(response == 2)
                {
                    doReturnCar();
                }
                else if(response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
    
    private void doPickupCar()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Merlion CARMS :: Customer Service :: Pickup Car***\n");
        List<RentalReservation> rentalReservations = rentalReservationSessionBeanRemote.retrieveRentalReservationsByPickupOutlet(currentEmployee.getOutlet().getOutletId());
        if (rentalReservations.isEmpty()) {
            System.out.println("No cars to be picked up!");
        } else {
            try {
                System.out.printf("%4s%20s%20s\n", "ID", "Start Date", "End Date");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (RentalReservation rentalReservation : rentalReservations) {
                    System.out.printf("%4s%20s%20s\n", rentalReservation.getRentalReservationId(), sdf.format(rentalReservation.getStartDateTime()), sdf.format(rentalReservation.getEndDateTime()));
                }
                System.out.print("Enter Rental Reservation ID> ");
                Long rentalReservationId = scanner.nextLong();
                scanner.nextLine();
                RentalReservation rentalReservation = rentalReservationSessionBeanRemote.retrieveRentalReservationByRentalReservationId(rentalReservationId);
                if (!rentalReservation.isPaid()) {
                    System.out.print("Pay rental fee? (Enter 'Y' to pay)> ");
                    String input = scanner.nextLine().trim();
                    if (!input.equals("Y")) {
                        throw new UnpaidRentalReservationException("Please pay for the rental reservation ID: " + rentalReservationId + " !");
                    } else {
                        System.out.println("Charged " + rentalReservation.getPrice().toString() + " to credit card: " + rentalReservation.getCustomer().getCreditCardNumber());
                    }
                }
                rentalReservationSessionBeanRemote.pickupCar(rentalReservationId);
                System.out.println("Car successfully picked up by customer");
            } catch (RentalReservationNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (UnpaidRentalReservationException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doReturnCar() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CarMS Management Client :: Sales Management :: Return Car***\n");
        List<RentalReservation> rentalReservations = rentalReservationSessionBeanRemote.retrieveRentalReservationsByReturnOutlet(currentEmployee.getOutlet().getOutletId());

        if (rentalReservations.isEmpty()) {
            System.out.println("No cars to be returned!");
        } else {
            System.out.printf("%4s%20s%20s\n", "ID", "Start Date", "End Date");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (RentalReservation rentalReservation : rentalReservations) {
                System.out.printf("%4s%20s%20s\n", rentalReservation.getRentalReservationId(), sdf.format(rentalReservation.getStartDateTime()), sdf.format(rentalReservation.getEndDateTime()));
            }
            System.out.print("Enter Rental Reservation ID> ");
            Long rentalReservationId = scanner.nextLong();
            scanner.nextLine();

            try {
                rentalReservationSessionBeanRemote.returnCar(rentalReservationId);
                System.out.println("Car returned by customer");
            } catch (RentalReservationNotFoundException ex) {
                System.out.println("No Rental Reservation of ID: " + rentalReservationId);
            }
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        }
    }
}
