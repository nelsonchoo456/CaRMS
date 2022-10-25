/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
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
public class RentalRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;
    private String name;
    private Long dayRate;
    private Long validityPeriod;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable  = false)
    private Category carCategory;

    public RentalRate() {
    }

    public RentalRate(String name, Long dayRate, Long validityPeriod, Category carCategory) {
        this.name = name;
        this.dayRate = dayRate;
        this.validityPeriod = validityPeriod;
        this.carCategory = carCategory;
    }
    
    
    
    
    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rateId != null ? rateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rateId fields are not set
        if (!(object instanceof RentalRate)) {
            return false;
        }
        RentalRate other = (RentalRate) object;
        if ((this.rateId == null && other.rateId != null) || (this.rateId != null && !this.rateId.equals(other.rateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RentalRate[ id=" + rateId + " ]";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the dayRate
     */
    public Long getDayRate() {
        return dayRate;
    }

    /**
     * @param dayRate the dayRate to set
     */
    public void setDayRate(Long dayRate) {
        this.dayRate = dayRate;
    }

    /**
     * @return the validityPeriod
     */
    public Long getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * @param validityPeriod the validityPeriod to set
     */
    public void setValidityPeriod(Long validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    /**
     * @return the carCategory
     */
    public Category getCarCategory() {
        return carCategory;
    }

    /**
     * @param carCategory the carCategory to set
     */
    public void setCarCategory(String carCategory) {
        this.setCarCategory(carCategory);
    }

    /**
     * @param carCategory the carCategory to set
     */
    public void setCarCategory(Category carCategory) {
        this.carCategory = carCategory;
    }
    
}
