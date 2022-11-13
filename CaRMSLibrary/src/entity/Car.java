/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import util.enumeration.CarStatusEnum;

/**
 *
 * @author lhern
 */
@Entity
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 16)
    private String licensePlateNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private CarStatusEnum status;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Model model;
    
    @OneToOne
    private RentalReservation rentalReservation;
    
    @ManyToOne
    private Outlet outlet;

    public Car() {
    }

    public Car(String licensePlateNumber, CarStatusEnum status) {
        this.licensePlateNumber = licensePlateNumber;
        this.status = status;
    }
    
    

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carId != null ? carId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the carId fields are not set
        if (!(object instanceof Car)) {
            return false;
        }
        Car other = (Car) object;
        if ((this.carId == null && other.carId != null) || (this.carId != null && !this.carId.equals(other.carId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Car[ id=" + carId + " ]";
    }

    /**
     * @return the licensePlateNumber
     */
    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    /**
     * @param licensePlateNumber the licensePlateNumber to set
     */
    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }
    
    /**
     * @return the status
     */
    public CarStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(CarStatusEnum status) {
        this.status = status;
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
     * @return the outlet
     */
    @XmlTransient
    public Outlet getOutlet() {
        return outlet;
    }

    /**
     * @param outlet the outlet to set
     */
    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    /**
     * @return the rentalReservation
     */
    @XmlTransient
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
