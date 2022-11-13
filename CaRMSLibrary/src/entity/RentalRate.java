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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import util.enumeration.RentalRateTypeEnum;

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
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String name;
    @Column(nullable = false, length = 32)
    @Min(0)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal ratePerDay;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RentalRateTypeEnum rentalRateType;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateTime;
    @Column(nullable = false)
    @NotNull
    private Boolean isEnabled;
    @Column(nullable = false)
    @NotNull
    private Boolean isUsed;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable  = false)
    private Category category;

    public RentalRate() {
        this.isEnabled = true;
        this.isUsed = false;
    }

    public RentalRate(String name, BigDecimal ratePerDay, RentalRateTypeEnum rentalRateType, Date startDateTime, Date endDateTime, Boolean isEnabled, Boolean isUsed) {
        this.name = name;
        this.ratePerDay = ratePerDay;
        this.rentalRateType = rentalRateType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isEnabled = isEnabled;
        this.isUsed = isUsed;
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
     * @return the ratePerDay
     */
    public BigDecimal getRatePerDay() {
        return ratePerDay;
    }

    /**
     * @param ratePerDay the ratePerDay to set
     */
    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
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
     * @return the rentalRateType
     */
    public RentalRateTypeEnum getRentalRateType() {
        return rentalRateType;
    }

    /**
     * @param rentalRateType the rentalRateType to set
     */
    public void setRentalRateType(RentalRateTypeEnum rentalRateType) {
        this.rentalRateType = rentalRateType;
    }

    /**
     * @return the isEnabled
     */
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * @return the isUsed
     */
    public Boolean getIsUsed() {
        return isUsed;
    }

    /**
     * @param isUsed the isUsed to set
     */
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    
}
