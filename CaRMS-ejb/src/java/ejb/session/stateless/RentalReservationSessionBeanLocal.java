/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalReservation;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface RentalReservationSessionBeanLocal {

    public Long createNewRentalReservation(Long categoryId, Long modelId, Long customerId, Long pickupOutletId, Long returnOutletId, RentalReservation rentalReservation) throws CategoryNotFoundException, ModelNotFoundException, CustomerNotFoundException, OutletNotFoundException;

    public RentalReservation retrieveRentalReservationByRentalReservationId(Long rentalReservationId) throws RentalReservationNotFoundException;

    public List<RentalReservation> retrieveAllRentalReservations();

    public Boolean searchCarByCategory(Date pickUpDateTime, Date returnDateTime, Long pickupOutletId, Long returnOutletId, Long carCategoryId) throws CategoryNotFoundException;

    public Boolean searchCarByModel(Date pickUpDateTime, Date returnDateTime, Long pickupOutletId, Long returnOutletId, Long modelId) throws ModelNotFoundException;

    public BigDecimal cancelReservation(Long rentalReservationId) throws RentalReservationNotFoundException;

    public List<RentalReservation> retrieveRentalReservationsByCustomer(Long customerId);

    public List<RentalReservation> retrieveRentalReservationsByPickupOutlet(Long outletId);

    public void pickupCar(Long reservationId) throws RentalReservationNotFoundException;

    public void returnCar(Long reservationId) throws RentalReservationNotFoundException;

    public List<RentalReservation> retrieveRentalReservationsByReturnOutlet(Long outletId);
    
}
