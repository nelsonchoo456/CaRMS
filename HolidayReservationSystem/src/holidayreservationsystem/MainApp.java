/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.reservation.Category;
import ws.client.reservation.CategoryNotFoundException;
import ws.client.reservation.CategoryNotFoundException_Exception;
import ws.client.reservation.Customer;
import ws.client.reservation.CustomerNotFoundException;
import ws.client.reservation.CustomerNotFoundException_Exception;
import ws.client.reservation.InputDataValidationException_Exception;
import ws.client.reservation.InvalidLoginCredentialException;
import ws.client.reservation.InvalidLoginCredentialException_Exception;
import ws.client.reservation.Model;
import ws.client.reservation.ModelNotFoundException;
import ws.client.reservation.ModelNotFoundException_Exception;
import ws.client.reservation.Outlet;
import ws.client.reservation.OutletNotFoundException;
import ws.client.reservation.OutletNotFoundException_Exception;
import ws.client.reservation.PartnerNotFoundException_Exception;
import ws.client.reservation.RentalRateNotFoundException;
import ws.client.reservation.RentalRateNotFoundException_Exception;
import ws.client.reservation.RentalReservation;
import ws.client.reservation.RentalReservationNotFoundException_Exception;
import ws.client.reservation.ReservationWebService;
import ws.client.reservation.ReservationWebService_Service;

/**
 *
 * @author Nelson Choo
 */
public class MainApp {
    
    private Long currentPartnerId = new Long(0);
    
    private ReservationWebService_Service reservationWebService_Service = new ReservationWebService_Service();
    private ReservationWebService reservationWebService = reservationWebService_Service.getReservationWebServicePort();
    
    void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Search Car");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        menuMain();
                    } catch (InvalidLoginCredentialException_Exception ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doSearchCar();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException_Exception {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.println("*** Holiday Reservation System :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            currentPartnerId = reservationWebService.partnerLogin(email, password);
        } else {
            InvalidLoginCredentialException ex = new InvalidLoginCredentialException();
            throw new InvalidLoginCredentialException_Exception("Missing login credential!", ex);
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
            System.out.println("*** Holiday Reservation System :: Search Car ***\n");
            System.out.print("Enter Pickup Date/Time (dd/mm/yyyy hh:mm)> ");
            pickupDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Return Date/Time (dd/mm/yyyy hh:mm)> ");
            returnDateTime = inputDateFormat.parse(scanner.nextLine().trim());

            List<Outlet> outlets = reservationWebService.retrieveAllOutlets();
            System.out.printf("%4s%64s%20s%20s\n", "ID", "Outlet Name", "Opening Hour", "Closing Hour");

            SimpleDateFormat operatingHours = new SimpleDateFormat("HH:mm");
            for (Outlet outlet : outlets) {
                String openingHour = "24/7";
                if (outlet.getOpeningHour() != null) {
                    openingHour = operatingHours.format(outlet.getOpeningHour().toGregorianCalendar().getTime());
                }
                String closingHour = "";
                if (outlet.getClosingHour() != null) {
                    closingHour = operatingHours.format(outlet.getClosingHour().toGregorianCalendar().getTime());
                }
                System.out.printf("%4s%64s%20s%20s\n", outlet.getOutletId(), outlet.getOutletName(), openingHour, closingHour);
            }

            System.out.print("Enter Pickup Outlet ID> ");
            pickupOutletId = scanner.nextLong();
            System.out.print("Enter Return Outlet ID> ");
            returnOutletId = scanner.nextLong();

            Outlet pickupOutlet = reservationWebService.retrieveOutletByOutletId(pickupOutletId);
            Outlet returnOutlet = reservationWebService.retrieveOutletByOutletId(returnOutletId);


            List<Category> categories = reservationWebService.retrieveAllCarCategories();
            
            List<Category> availableCategories = new ArrayList<Category>();
            
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(pickupDateTime);
            XMLGregorianCalendar pickupDT = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            
            c.setTime(returnDateTime);
            XMLGregorianCalendar returnDT = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);


            for (Category category : categories) {
                canReserve = reservationWebService.searchCarByCategory(pickupDT, returnDT, pickupOutletId, returnOutletId, category.getCategoryId());
                if (canReserve) {
                    availableCategories.add(category);
                }
            }
            List<Model> models = reservationWebService.retrieveAllModels();
            List<Model> availableModels = new ArrayList<Model>();
            
            for (Model model : models) {
                canReserve = reservationWebService.searchCarByModel(pickupDT, returnDT, pickupOutletId, returnOutletId, model.getModelId());
                if (canReserve) {
                    availableModels.add(model);
                }
            }
                
            if (availableCategories.isEmpty() && availableModels.isEmpty()) {
                System.out.println("No cars are available under the provided criteria!");
            } else {
                System.out.println("Congrats! There are cars available for your timing! \n");
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (OutletNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (ModelNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (CategoryNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation System ***\n");
            System.out.println("1: Search Car");
            System.out.println("2: Reserve Car");
            System.out.println("3: View Reservation Details");
            System.out.println("4: View All Partner Reservations");
            System.out.println("5: Logout\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doSearchCar();
                } else if (response == 2) {
                    doReserveCar();
                } else if (response == 3) {
                    doViewReservationDetails();
                } else if (response == 4) {
                    doViewAllMyReservations();
                } else if (response == 5) {
                    break;
                }
                else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 5) {
                break;
            }
        }
    }
    
    private void doReserveCar()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CARMS Reservation Client :: Reserve Car ***\n");

        RentalReservation rentalReservation = new RentalReservation();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y hh:mm");
        
        GregorianCalendar c = new GregorianCalendar();
        
        try {
            System.out.print("Enter Car Category ID> ");
            Long categoryId = scanner.nextLong();
            
            System.out.print("Enter Model ID (Enter 0 if no model)> ");
            Long modelId = scanner.nextLong();
            scanner.nextLine();
            
            System.out.print("Enter Pickup Date/Time (dd/mm/yyyy hh:mm)> ");
            Date pickupDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            
            c.setTime(pickupDateTime);
            XMLGregorianCalendar pickupDT = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            
            rentalReservation.setStartDateTime(pickupDT);
            System.out.print("Enter Pickup Outlet ID> ");
            Long pickupOutletId = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Enter Return Date/Time (dd/mm/yyyy hh:mm)> ");
            Date returnDateTime = inputDateFormat.parse(scanner.nextLine().trim());
            
            c.setTime(returnDateTime);
            XMLGregorianCalendar returnDT = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            
            rentalReservation.setEndDateTime(returnDT);
            System.out.print("Enter Return Outlet ID> ");
            Long returnOutletId = scanner.nextLong();
            scanner.nextLine();
            
            BigDecimal totalRentalFee = reservationWebService.calculateTotalRentalFee(categoryId, pickupDT, returnDT);
            rentalReservation.setPrice(totalRentalFee);
            
            Customer newCustomer = new Customer();

            System.out.print("Enter customer first name> ");
            newCustomer.setFirstName(scanner.nextLine().trim());
            System.out.print("Enter customer last name> ");
            newCustomer.setLastName(scanner.nextLine().trim());
            System.out.print("Enter email> ");
            newCustomer.setEmail(scanner.nextLine().trim());
            System.out.print("Enter password> ");
            newCustomer.setPassword(scanner.nextLine().trim());
            System.out.print("Enter credit card number> ");
            newCustomer.setCreditCardNumber(scanner.nextLine().trim());
            
            System.out.print("Would you like to pay now? (Enter 'Y' to enter payment details)> ");
            String input = scanner.nextLine().trim();
            
            if (input.equals("Y")) {
                rentalReservation.setPaid(true);
                System.out.println("Charged " + totalRentalFee.toString() + " to credit card: " + newCustomer.getCreditCardNumber());
            } else {
                rentalReservation.setPaid(Boolean.FALSE);
            }
            
            Long customerId = reservationWebService.createNewCustomer(currentPartnerId, newCustomer);
            
            
            Long rentalReservationId = reservationWebService.createNewPartnerRentalReservation(categoryId, currentPartnerId, modelId, customerId, pickupOutletId, returnOutletId, rentalReservation);
            System.out.println("Rental reservation created with ID: " + rentalReservationId);

        } catch (RentalRateNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        } catch (CategoryNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (OutletNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (ModelNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (CustomerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        } catch (InputDataValidationException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doViewAllMyReservations()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System : View All My Reservations ***\n");
        
        List<RentalReservation> reservations = reservationWebService.retrievePartnerRentalReservations(currentPartnerId);
        
        System.out.printf("%30s%18s%40s%40s%30s%18s\n", "Rental Reservation ID", "Rental Fee", "Start Date Time", "End Date Time", "Paid", "isCancelled");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (RentalReservation rentalReservation : reservations) {
            XMLGregorianCalendar startGregorianCalendar = rentalReservation.getStartDateTime();
            XMLGregorianCalendar endGregorianCalendar = rentalReservation.getEndDateTime();
            Date startDate = startGregorianCalendar.toGregorianCalendar().getTime();
            Date endDate = endGregorianCalendar.toGregorianCalendar().getTime();
            
            System.out.printf("%30s%18s%40s%40s%30s%18s\n", rentalReservation.getRentalReservationId(), rentalReservation.getPrice(), sdf.format(startDate), sdf.format(endDate), String.valueOf(rentalReservation.isPaid()), String.valueOf(rentalReservation.isIsCancelled()));
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewReservationDetails()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System : View Reservation Details ***\n");
        
        System.out.print("Enter Reservation ID> ");
        
        Long reservationId = scanner.nextLong();
        scanner.nextLine();
        
        try {
            RentalReservation rentalReservation = reservationWebService.retrieveRentalReservationByRentalReservationId(reservationId);
            
            XMLGregorianCalendar startGregorianCalendar = rentalReservation.getStartDateTime();
            XMLGregorianCalendar endGregorianCalendar = rentalReservation.getEndDateTime();
            Date startDate = startGregorianCalendar.toGregorianCalendar().getTime();
            Date endDate = endGregorianCalendar.toGregorianCalendar().getTime();
            
            System.out.printf("%30s%18s%40s%40s%30s%18s\n", "Rental Reservation ID", "Rental Fee", "Start Date Time", "End Date Time", "Paid", "isCancelled");
            System.out.printf("%30s%18s%40s%40s%30s%18s\n", rentalReservation.getRentalReservationId(), rentalReservation.getPrice(), startDate, endDate, String.valueOf(rentalReservation.isPaid()), String.valueOf(rentalReservation.isIsCancelled()));
            
            System.out.print("Would you like to cancel the reservation? (Enter 'Y' to enter cancel the reservation)> ");
            String input = scanner.nextLine().trim();
            
            if (input.equals("Y")) {
                doCancelReservation(reservationId);
            } else {
                System.out.print("Press any key to continue...> ");
            }
        } catch (RentalReservationNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doCancelReservation(Long reservationId)
    {
        Scanner scanner = new Scanner(System.in);
        RentalReservation rentalReservation;

        System.out.println("*** Holiday Reservation Client :: Cancel Reservation ***\n");
        try {
            BigDecimal penalty = reservationWebService.cancelReservation(reservationId);
            rentalReservation = reservationWebService.retrieveRentalReservationByRentalReservationId(reservationId);
            
            System.out.println("Reservation successfully cancelled!");

            if (rentalReservation.isPaid()) {
                System.out.println("You have been refunded SGD $" + rentalReservation.getPrice().subtract(penalty) + " after deducting cancellation penalty of SGD" + penalty + ".");
            } else {
                System.out.println("Your card : " + " has been charged SGD $" + penalty + " as a cancellation penalty.");
            }
        } catch (RentalReservationNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    
    
}
