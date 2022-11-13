/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author lhern
 */
@Entity
public class RentalReservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalReservationId;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @Digits(integer = 9, fraction = 2)
    private BigDecimal price;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateTime;
    @Column(nullable = false)
    @NotNull
    private boolean paid;
    @Column(nullable = false)
    @NotNull
    private boolean isCancelled;
    @Column(nullable = false)
    @NotNull
    private boolean isComplete;
    @Column(nullable = false)
    @NotNull
    private boolean isPicked;
    
    
    @OneToOne
    private Model model;
    
    @OneToOne
    private Category category;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @ManyToOne
    private Car car;
    
    @OneToOne(optional = false)
    private Outlet pickupOutlet;
    
    @OneToOne(optional = false)
    private Outlet returnOutlet;
    
    @OneToOne
    private DispatchRecord dispatchRecord;
    
    @ManyToOne
    private RentalRate rentalRate;
    
    @ManyToOne
    private Partner partner;

    public RentalReservation() {
        this.isCancelled = false;
        this.isComplete = false;
        this.isPicked = false;
    }

    public RentalReservation(BigDecimal price, Date startDateTime, Date endDateTime, boolean paid) {
        this.price = price;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.paid = paid;
        this.isCancelled = false;
        this.isComplete = false;
        this.isPicked = false;
    }

    public Long getRentalReservationId() {
        return rentalReservationId;
    }

    public void setRentalReservationId(Long rentalReservationId) {
        this.rentalReservationId = rentalReservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rentalReservationId != null ? rentalReservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rentalReservationId fields are not set
        if (!(object instanceof RentalReservation)) {
            return false;
        }
        RentalReservation other = (RentalReservation) object;
        if ((this.rentalReservationId == null && other.rentalReservationId != null) || (this.rentalReservationId != null && !this.rentalReservationId.equals(other.rentalReservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RentalRecord[ id=" + rentalReservationId + " ]";
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the startDateTime
     */
    public Date getStartDateTime() {
        return startDateTime;
    }

    /**
     * @param startDateTime the startDateTime to set
     */
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * @return the endDateTime
     */
    public Date getEndDateTime() {
        return endDateTime;
    }

    /**
     * @param endDateTime the endDateTime to set
     */
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * @return the paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * @param paid the paid to set
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    /**
     * @return the isCancelled
     */
    public boolean isIsCancelled() {
        return isCancelled;
    }

    /**
     * @param isCancelled the isCancelled to set
     */
    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    /**
     * @return the isComplete
     */
    public boolean isIsComplete() {
        return isComplete;
    }

    /**
     * @param isComplete the isComplete to set
     */
    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * @return the isPicked
     */
    public boolean isIsPicked() {
        return isPicked;
    }

    /**
     * @param isPicked the isPicked to set
     */
    public void setIsPicked(boolean isPicked) {
        this.isPicked = isPicked;
    }

    /**
     * @return the model
     */
    @XmlTransient
    public Model getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return the category
     */
    @XmlTransient
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @return the customer
     */
    @XmlTransient
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * @return the car
     */
    @XmlTransient
    public Car getCar() {
        return car;
    }

    /**
     * @param car the car to set
     */
    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * @return the pickupOutlet
     */
    @XmlTransient
    public Outlet getPickupOutlet() {
        return pickupOutlet;
    }

    /**
     * @param pickupOutlet the pickupOutlet to set
     */
    public void setPickupOutlet(Outlet pickupOutlet) {
        this.pickupOutlet = pickupOutlet;
    }

    /**
     * @return the returnOutlet
     */
    @XmlTransient
    public Outlet getReturnOutlet() {
        return returnOutlet;
    }

    /**
     * @param returnOutlet the returnOutlet to set
     */
    public void setReturnOutlet(Outlet returnOutlet) {
        this.returnOutlet = returnOutlet;
    }

    /**
     * @return the dispatchRecord
     */
    @XmlTransient
    public DispatchRecord getDispatchRecord() {
        return dispatchRecord;
    }

    /**
     * @param dispatchRecord the dispatchRecord to set
     */
    public void setDispatchRecord(DispatchRecord dispatchRecord) {
        this.dispatchRecord = dispatchRecord;
    }

    /**
     * @return the rentalRate
     */
    @XmlTransient
    public RentalRate getRentalRate() {
        return rentalRate;
    }

    /**
     * @param rentalRate the rentalRate to set
     */
    public void setRentalRate(RentalRate rentalRate) {
        this.rentalRate = rentalRate;
    }

    /**
     * @return the partner
     */
    @XmlTransient
    public Partner getPartner() {
        return partner;
    }

    /**
     * @param partner the partner to set
     */
    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    
}
