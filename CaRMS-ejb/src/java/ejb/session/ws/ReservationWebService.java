/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RentalReservationSessionBeanLocal;
import entity.Category;
import entity.Customer;
import entity.Model;
import entity.Outlet;
import entity.Partner;
import entity.RentalReservation;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.PartnerNotFoundException;
import util.exception.RentalRateNotFoundException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@WebService(serviceName = "ReservationWebService")
@Stateless()
public class ReservationWebService {

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @EJB
    private RentalReservationSessionBeanLocal rentalReservationSessionBean;

    @EJB
    private ModelSessionBeanLocal modelSessionBean;

    @EJB
    private CategorySessionBeanLocal categorySessionBean;

    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    /**
     * This is a sample web service operation
     */
    @WebMethod
    public Long createNewCustomer(@WebParam Long partnerId, @WebParam Customer newCustomer) throws InputDataValidationException, PartnerNotFoundException
    {
        return customerSessionBean.createNewCustomerWithPartner(partnerId, newCustomer);
    }
    
    @WebMethod
    public Long createNewPartner(@WebParam Partner newPartner) throws InputDataValidationException {
        return partnerSessionBean.createNewPartner(newPartner);
    }
    
    @WebMethod
    public Long partnerLogin(@WebParam String partnerName, @WebParam String password) throws InvalidLoginCredentialException {
        return partnerSessionBean.partnerLogin(partnerName, password);
    }
    
    @WebMethod
    public Partner retrievePartnerByPartnerId(@WebParam Long partnerId) throws PartnerNotFoundException {
        return partnerSessionBean.retrievePartnerByPartnerId(partnerId);
    }
    
    @WebMethod
    public Outlet retrieveOutletByOutletId(@WebParam Long outletId) throws OutletNotFoundException {
        return outletSessionBean.retrieveOutletById(outletId);
    }
    
    @WebMethod
    public Category retrieveCategoryByCategoryId(@WebParam Long carCategoryId) throws CategoryNotFoundException {
        return categorySessionBean.retrieveCategoryById(carCategoryId);
    }
    
    @WebMethod
    public Model retrieveModelByModelId(@WebParam Long modelId) throws ModelNotFoundException {
        return modelSessionBean.retrieveModelById(modelId);
    }
    
    @WebMethod
    public BigDecimal calculateTotalRentalFee(@WebParam Long carCategoryId, @WebParam Date pickUpDateTime, @WebParam Date returnDateTime) throws CategoryNotFoundException, RentalRateNotFoundException {
        return categorySessionBean.calculateTotalRentalFee(carCategoryId, pickUpDateTime, returnDateTime);
    }
    
    @WebMethod
    public RentalReservation retrieveRentalReservationByRentalReservationId(@WebParam Long rentalReservationId) throws RentalReservationNotFoundException {
        return rentalReservationSessionBean.retrieveRentalReservationByRentalReservationId(rentalReservationId);
    }
    
    @WebMethod
    public List<RentalReservation> retrieveAllRentalReservations() {
        return rentalReservationSessionBean.retrieveAllRentalReservations();
    }
    
    @WebMethod
    public List<RentalReservation> retrievePartnerRentalReservations(@WebParam Long partnerId) {
        return rentalReservationSessionBean.retrievePartnerRentalReservations(partnerId);
    }
    
    @WebMethod
    public Long createNewRentalReservation(@WebParam Long carCategoryId, @WebParam Long modelId, @WebParam Long customerId, @WebParam Long pickupOutletId, @WebParam Long returnOutletId, @WebParam RentalReservation newRentalReservation) throws OutletNotFoundException, CustomerNotFoundException, InputDataValidationException, CategoryNotFoundException, ModelNotFoundException {
        return rentalReservationSessionBean.createNewRentalReservation(carCategoryId, modelId, customerId, pickupOutletId, returnOutletId, newRentalReservation);
    }
    
    @WebMethod
    public Long createNewPartnerRentalReservation(@WebParam Long carCategoryId, @WebParam Long partnerId, @WebParam Long modelId, @WebParam Long customerId, @WebParam Long pickupOutletId, @WebParam Long returnOutletId, @WebParam RentalReservation newRentalReservation) throws OutletNotFoundException, CustomerNotFoundException, InputDataValidationException, CategoryNotFoundException, ModelNotFoundException, PartnerNotFoundException {
        return rentalReservationSessionBean.createNewPartnerRentalReservation(carCategoryId, partnerId, modelId, customerId, pickupOutletId, returnOutletId, newRentalReservation);
    }
    
    @WebMethod
    public BigDecimal cancelReservation(@WebParam Long rentalReservationId) throws RentalReservationNotFoundException {
        return rentalReservationSessionBean.cancelReservation(rentalReservationId);
    }
    
    @WebMethod
    public Boolean searchCarByCategory(@WebParam Date pickUpDateTime, @WebParam Date returnDateTime, @WebParam Long pickupOutletId, @WebParam Long returnOutletId, @WebParam Long carCategoryId) throws CategoryNotFoundException, OutletNotFoundException {
        return rentalReservationSessionBean.searchCarByCategory(pickUpDateTime, returnDateTime, pickupOutletId, returnOutletId, carCategoryId);
    }
    
    @WebMethod
    public Boolean searchCarByModel(@WebParam Date pickUpDateTime, @WebParam Date returnDateTime, @WebParam Long pickupOutletId, @WebParam Long returnOutletId, @WebParam Long modelId) throws CategoryNotFoundException, OutletNotFoundException, ModelNotFoundException {
        return rentalReservationSessionBean.searchCarByModel(pickUpDateTime, returnDateTime, pickupOutletId, returnOutletId, modelId);
    }
    
    @WebMethod
    public List<Model> retrieveAllModels()  {
        return modelSessionBean.viewAllModels();
    }
    
    @WebMethod
    public List<Category> retrieveAllCarCategories() {
        return categorySessionBean.viewAllCategories();
    }
    
    @WebMethod
    public List<Outlet> retrieveAllOutlets() {
        return outletSessionBean.viewAllOutlets();
    }
}
