/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Nelson Choo
 */
@Entity
public class DispatchRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dispatchRecordId;
    
    private Boolean isCompleted;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transitDate;
    
    @ManyToOne
    private Employee driver;
    @ManyToOne(optional = false)
    
    @OneToOne(optional = false)
    private RentalReservation rentalReservation;

    public DispatchRecord() {
        this.isCompleted = false;
    }

    public DispatchRecord(Date transitDate) {
        this.isCompleted = false;
        this.transitDate = transitDate;
    }
    
    

    public Long getDispatchRecordId() {
        return dispatchRecordId;
    }

    public void setDispatchRecordId(Long dispatchRecordId) {
        this.dispatchRecordId = dispatchRecordId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dispatchRecordId != null ? dispatchRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the dispatchRecordId fields are not set
        if (!(object instanceof DispatchRecord)) {
            return false;
        }
        DispatchRecord other = (DispatchRecord) object;
        if ((this.dispatchRecordId == null && other.dispatchRecordId != null) || (this.dispatchRecordId != null && !this.dispatchRecordId.equals(other.dispatchRecordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DispatchRecord[ id=" + dispatchRecordId + " ]";
    }

    /**
     * @return the isCompleted
     */
    public Boolean getIsCompleted() {
        return isCompleted;
    }

    /**
     * @param isCompleted the isCompleted to set
     */
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * @return the transitDate
     */
    public Date getTransitDate() {
        return transitDate;
    }

    /**
     * @param transitDate the transitDate to set
     */
    public void setTransitDate(Date transitDate) {
        this.transitDate = transitDate;
    }

    /**
     * @return the driver
     */
    public Employee getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(Employee driver) {
        this.driver = driver;
    }

    /**
     * @return the rentalReservation
     */
    public RentalReservation getRentalReservation() {
        return rentalReservation;
    }

    /**
     * @param rentalReservation the rentalReservation to set
     */
    public void setRentalReservation(RentalReservation rentalReservation) {
        this.rentalReservation = rentalReservation;
    }
    
}
