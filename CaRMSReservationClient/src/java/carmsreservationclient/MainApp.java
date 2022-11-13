/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.RentalReservationSessionBeanRemote;
import entity.Category;
import entity.Customer;
import entity.Model;
import entity.Outlet;
import entity.RentalReservation;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.RentalRateNotFoundException;
import util.exception.RentalReservationNotFoundException;

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
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private RentalReservationSessionBeanRemote rentalReservationSessionBeanRemote;

    private Customer currentCustomer;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public MainApp() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public MainApp(OutletSessionBeanRemote outletSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, ModelSessionBeanRemote modelSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, RentalReservationSessionBeanRemote rentalReservationSessionBeanRemote) {
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.rentalReservationSessionBeanRemote = rentalReservationSessionBeanRemote;
        
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to CARMS Reservation System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Register");
            System.out.println("3: Search Car");
            System.out.println("4: Exit\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        menuMain();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegisterCustomer();
                } else if (response == 3) {
                    doSearchCar();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.println("*** CARMS Reservation System :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            currentCustomer = customerSessionBeanRemote.customerLogin(email, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doSearchCar() {
        Scanner scanner = new Scanner(System.in);
        Date pickupDateTime;
        Long pickupOutletId;
        Date returnDateTime;
        Long returnOutletId;
        Boolean canReserve = false;
        Long categoryId = new Long(0);
        Long modelId = new Long(0);

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y hh:mm");

        try {
            System.out.println("*** CARMS Reservation System :: Search Car ***\n");
            System.out.print("Enter Pickup Date/Time (dd/mm/yyyy hh:mm)> ");
            pickupDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Return Date/Time (dd/mm/yyyy hh:mm)> ");
            returnDateTime = inputDateFormat.parse(scanner.nextLine().trim());

            List<Outlet> outlets = outletSessionBeanRemote.viewAllOutlets();
            System.out.printf("%4s%64s%20s%20s\n", "ID", "Outlet Name", "Opening Hour", "Closing Hour");

            SimpleDateFormat operatingHours = new SimpleDateFormat("HH:mm");
            for (Outlet outlet : outlets) {
                String openingHour = "24/7";
                if (outlet.getOpeningHour() != null) {
                    openingHour = operatingHours.format(outlet.getOpeningHour());
                }
                String closingHour = "";
                if (outlet.getClosingHour() != null) {
                    closingHour = operatingHours.format(outlet.getClosingHour());
                }
                System.out.printf("%4s%64s%20s%20s\n", outlet.getOutletId(), outlet.getOutletName(), openingHour, closingHour);
            }

            System.out.print("Enter Pickup Outlet ID> ");
            pickupOutletId = scanner.nextLong();
            System.out.print("Enter Return Outlet ID> ");
            returnOutletId = scanner.nextLong();

            Outlet pickupOutlet = outletSessionBeanRemote.retrieveOutletById(pickupOutletId);
            Outlet returnOutlet = outletSessionBeanRemote.retrieveOutletById(returnOutletId);


            List<Category> categories = categorySessionBeanRemote.viewAllCategories();
            
            List<Category> availableCategories = new ArrayList<Category>();

            for (Category category : categories) {
                canReserve = rentalReservationSessionBeanRemote.searchCarByCategory(pickupDateTime, returnDateTime, pickupOutletId, returnOutletId, category.getCategoryId());
                if (canReserve) {
                    availableCategories.add(category);
                }
            }
            List<Model> models = modelSessionBeanRemote.viewAllModels();
            List<Model> availableModels = new ArrayList<Model>();
            
            for (Model model : models) {
                canReserve = rentalReservationSessionBeanRemote.searchCarByModel(pickupDateTime, returnDateTime, pickupOutletId, returnOutletId, model.getModelId());
                if (canReserve) {
                    availableModels.add(model);
                }
            }
                
            if (availableCategories.isEmpty() && availableModels.isEmpty()) {
                System.out.println("No cars are available under the provided criteria!");
            } else {
                System.out.printf("%4s%64s%64s\n", "ID", "Category Name", "Total Rental Fee");
                for (Category category : availableCategories) {
                    BigDecimal totalRentalFee = categorySessionBeanRemote.calculateTotalRentalFee(category.getCategoryId(), pickupDateTime, returnDateTime);
                    System.out.printf("%4s%64s%64s\n", category.getCategoryId(), category.getCategoryName(), totalRentalFee);
                }
                
                System.out.println("\n");
                
                System.out.printf("%4s%64s%32s%32s%32s\n", "ID", "Category Name", "Make", "Model", "Total Rental Fee");
                for (Model model : availableModels) {
                    BigDecimal totalRentalFee = categorySessionBeanRemote.calculateTotalRentalFee(model.getCategory().getCategoryId(), pickupDateTime, returnDateTime);
                    System.out.printf("%4s%64s%32s%32s%32s\n", model.getModelId(), model.getCategory().getCategoryName(), model.getMake(), model.getModel(), totalRentalFee);
                }
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (OutletNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ModelNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (CategoryNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (RentalRateNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }
    
    private void doReserveCar(Long categoryId, Long modelId, Date pickUpDateTime, Date returnDateTime, Long pickupOutletId, Long returnOutletId, BigDecimal totalRentalFee)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CARMS Reservation Client :: Reserve Car ***\n");

        RentalReservation rentalReservation = new RentalReservation();
        
        rentalReservation.setStartDateTime(pickUpDateTime);
        rentalReservation.setEndDateTime(returnDateTime);
        rentalReservation.setPrice(totalRentalFee);
            
        System.out.print("Would you like to pay now? (Enter 'Y' to enter payment details)> ");
        String input = scanner.nextLine().trim();
            
        if (input.equals("Y")) {
            rentalReservation.setPaid(true);
            System.out.println("Charged " + totalRentalFee.toString() + " to credit card: " + currentCustomer.getCreditCardNumber());
        } else {
            rentalReservation.setPaid(Boolean.FALSE);
        }
        
        Set<ConstraintViolation<RentalReservation>>constraintViolations = validator.validate(rentalReservation);
        
        if (constraintViolations.isEmpty()) {
            try {
                Long rentalReservationId = rentalReservationSessionBeanRemote.createNewRentalReservation(categoryId, modelId, currentCustomer.getCustomerId(), pickupOutletId, returnOutletId, rentalReservation);
                System.out.println("Rental reservation created with ID: " + rentalReservationId);
            } catch (CategoryNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (ModelNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (CustomerNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (OutletNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (InputDataValidationException ex) {
                System.out.println("Invalid input data! \n");
            }
        } else {
            showInputDataValidationErrorsForRentalReservation(constraintViolations);
        }
    }
    
    private void showInputDataValidationErrorsForRentalReservation(Set<ConstraintViolation<RentalReservation>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void doReserveCar()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CARMS Reservation Client :: Reserve Car ***\n");

        RentalReservation rentalReservation = new RentalReservation();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y hh:mm");
        
        try {
            System.out.print("Enter Car Category ID> ");
            Long categoryId = scanner.nextLong();
            
            System.out.print("Enter Model ID (Enter 0 if no model)> ");
            Long modelId = scanner.nextLong();
            scanner.nextLine();
            
            System.out.print("Enter Pickup Date/Time (dd/mm/yyyy hh:mm)> ");
            Date pickupDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            rentalReservation.setStartDateTime(pickupDateTime);
            System.out.print("Enter Pickup Outlet ID> ");
            Long pickupOutletId = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Enter Return Date/Time (dd/mm/yyyy hh:mm)> ");
            Date returnDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            rentalReservation.setEndDateTime(returnDateTime);
            System.out.print("Enter Return Outlet ID> ");
            Long returnOutletId = scanner.nextLong();
            scanner.nextLine();
            
            BigDecimal totalRentalFee = categorySessionBeanRemote.calculateTotalRentalFee(categoryId, pickupDateTime, returnDateTime);
            rentalReservation.setPrice(totalRentalFee);
            
            System.out.print("Would you like to pay now? (Enter 'Y' to enter payment details)> ");
            String input = scanner.nextLine().trim();
            
            if (input.equals("Y")) {
                rentalReservation.setPaid(true);
                System.out.println("Charged " + totalRentalFee.toString() + " to credit card: " + currentCustomer.getCreditCardNumber());
            } else {
                rentalReservation.setPaid(Boolean.FALSE);
            }
            
            Set<ConstraintViolation<RentalReservation>>constraintViolations = validator.validate(rentalReservation);
            
            if (constraintViolations.isEmpty()) {
                Long rentalReservationId = rentalReservationSessionBeanRemote.createNewRentalReservation(categoryId, modelId, currentCustomer.getCustomerId(), pickupOutletId, returnOutletId, rentalReservation);
                System.out.println("Rental reservation created with ID: " + rentalReservationId);
            } else {
                showInputDataValidationErrorsForRentalReservation(constraintViolations);
            }
        } catch (RentalRateNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        } catch (CategoryNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (OutletNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ModelNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (CustomerNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (InputDataValidationException ex) {
            System.out.println("Invalid input data! \n");
        }
    }

    private void doRegisterCustomer() {
        Scanner scanner = new Scanner(System.in);
        Customer newCustomer = new Customer();

        System.out.println("*** CARMS Reservation Client :: Register New Customer ***\n");

        System.out.print("Enter First Name> ");
        newCustomer.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newCustomer.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Email> ");
        newCustomer.setEmail(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newCustomer.setPassword(scanner.nextLine().trim());
        System.out.print("Enter Credit Card Number> ");
        newCustomer.setCreditCardNumber(scanner.nextLine().trim());
        
        Set<ConstraintViolation<Customer>>constraintViolations = validator.validate(newCustomer);
        
        if (constraintViolations.isEmpty()) {
            try {
                Long newCustomerId = customerSessionBeanRemote.createNewCustomer(newCustomer);
                System.out.println("New customer created successfully!: " + newCustomerId.toString() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println("Invalid input data! \n");
            }
        } else {
            showInputDataValidationErrorsForCustomer(constraintViolations);
        }
    }
    
    private void showInputDataValidationErrorsForCustomer(Set<ConstraintViolation<Customer>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CARMS Reservation System ***\n");
            System.out.println("You are login as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "\n");
            System.out.println("1: Reserve Car");
            System.out.println("2: Search Car");
            System.out.println("3: View Reservation Details");
            System.out.println("4: View All My Reservations");
            System.out.println("5: Logout\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doReserveCar();
                } else if (response == 2) {
                    doSearchCar();
                } else if (response == 3) {
                    doViewReservationDetails();
                } else if (response == 4) {
                    doViewAllMyReservations();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }
    
    private void doViewAllMyReservations()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CARMS Reservation System : View All My Reservations ***\n");
        
        List<RentalReservation> reservations = rentalReservationSessionBeanRemote.retrieveRentalReservationsByCustomer(currentCustomer.getCustomerId());
        
        System.out.printf("%30s%18s%40s%40s%30s%18s%18s\n", "Rental Reservation ID", "Rental Fee", "Start Date Time", "End Date Time", "Paid", "isCancelled", "Car Category");
        
        for (RentalReservation rentalReservation : reservations) {
            System.out.printf("%30s%18s%40s%40s%30s%18s%18s\n", rentalReservation.getRentalReservationId(), rentalReservation.getPrice(), rentalReservation.getStartDateTime(), rentalReservation.getEndDateTime(), String.valueOf(rentalReservation.isPaid()), String.valueOf(rentalReservation.isIsCancelled()), rentalReservation.getCategory().getCategoryName());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewReservationDetails()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CARMS Reservation System : View Reservation Details ***\n");
        
        System.out.print("Enter Reservation ID> ");
        
        Long reservationId = scanner.nextLong();
        scanner.nextLine();
        
        try {
            RentalReservation rentalReservation = rentalReservationSessionBeanRemote.retrieveRentalReservationByRentalReservationId(reservationId);
            System.out.printf("%30s%18s%40s%40s%30s%18s%18s\n", "Rental Reservation ID", "Rental Fee", "Start Date Time", "End Date Time", "Paid", "isCancelled", "Car Category");
            System.out.printf("%30s%18s%40s%40s%30s%18s%18s\n", rentalReservation.getRentalReservationId(), rentalReservation.getPrice(), rentalReservation.getStartDateTime(), rentalReservation.getEndDateTime(), String.valueOf(rentalReservation.isPaid()), String.valueOf(rentalReservation.isIsCancelled()), rentalReservation.getCategory().getCategoryName());
            
            System.out.print("Would you like to cancel the reservation? (Enter 'Y' to enter cancel the reservation)> ");
            String input = scanner.nextLine().trim();
            
            if (input.equals("Y")) {
                doCancelReservation(reservationId);
            } else {
                System.out.print("Press any key to continue...> ");
            }
        } catch (RentalReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doCancelReservation(Long reservationId)
    {
        Scanner scanner = new Scanner(System.in);
        RentalReservation rentalReservation;

        System.out.println("*** CARMS Reservation Client :: Cancel Reservation ***\n");
        try {
            BigDecimal penalty = rentalReservationSessionBeanRemote.cancelReservation(reservationId);
            rentalReservation = rentalReservationSessionBeanRemote.retrieveRentalReservationByRentalReservationId(reservationId);
            
            System.out.println("Reservation successfully cancelled!");

            if (rentalReservation.isPaid()) {
                System.out.println("You have been refunded SGD $" + rentalReservation.getPrice().subtract(penalty) + " to your card " + currentCustomer.getCreditCardNumber() + " after deducting cancellation penalty of SGD" + penalty + ".");
            } else {
                System.out.println("Your card : " + currentCustomer.getCreditCardNumber() + " has been charged SGD $" + penalty + " as a cancellation penalty.");
            }
        } catch (RentalReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}
