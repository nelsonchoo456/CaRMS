/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author lhern
 */
@Entity
public class RentalRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;
    private Long rentalFee;
    private Long penaltyFee;
    private Timestamp rentalTime;
    private Timestamp returnTime;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Model model;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;

    public RentalRecord() {
    }
    
    
    

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rentalId != null ? rentalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rentalId fields are not set
        if (!(object instanceof RentalRecord)) {
            return false;
        }
        RentalRecord other = (RentalRecord) object;
        if ((this.rentalId == null && other.rentalId != null) || (this.rentalId != null && !this.rentalId.equals(other.rentalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RentalRecord[ id=" + rentalId + " ]";
    }

    /**
     * @return the rentalFee
     */
    public Long getRentalFee() {
        return rentalFee;
    }

    /**
     * @param rentalFee the rentalFee to set
     */
    public void setRentalFee(Long rentalFee) {
        this.rentalFee = rentalFee;
    }

    /**
     * @return the penaltyFee
     */
    public Long getPenaltyFee() {
        return penaltyFee;
    }

    /**
     * @param penaltyFee the penaltyFee to set
     */
    public void setPenaltyFee(Long penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    /**
     * @return the rentalTime
     */
    public Timestamp getRentalTime() {
        return rentalTime;
    }

    /**
     * @param rentalTime the rentalTime to set
     */
    public void setRentalTime(Timestamp rentalTime) {
        this.rentalTime = rentalTime;
    }

    /**
     * @return the returnTime
     */
    public Timestamp getReturnTime() {
        return returnTime;
    }

    /**
     * @param returnTime the returnTime to set
     */
    public void setReturnTime(Timestamp returnTime) {
        this.returnTime = returnTime;
    }

    /**
     * @return the model
     */
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
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
}
