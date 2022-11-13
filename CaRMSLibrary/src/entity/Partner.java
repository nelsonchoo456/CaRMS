/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Nelson Choo
 */
@Entity
public class Partner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false, length = 64)
    private String password;
    
    @OneToMany(mappedBy = "partner")
    private List<Customer> customers;
    
    @OneToMany(mappedBy = "partner")
    private List<RentalReservation> rentalReservations;

    public Partner() {
        this.customers = new ArrayList<Customer>();
        this.rentalReservations = new ArrayList<RentalReservation>();
    }

    public Partner(String name, String password) {
        this.name = name;
        this.password = password;
        this.customers = new ArrayList<Customer>();
        this.rentalReservations = new ArrayList<RentalReservation>();
    }
    
    
    
    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerId != null ? partnerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof Partner)) {
            return false;
        }
        Partner other = (Partner) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Partner[ id=" + partnerId + " ]";
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
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the customers
     */
    @XmlTransient
    public List<Customer> getCustomers() {
        return customers;
    }

    /**
     * @param customers the customers to set
     */
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    /**
     * @return the rentalReservations
     */
    @XmlTransient
    public List<RentalReservation> getRentalReservations() {
        return rentalReservations;
    }

    /**
     * @param rentalReservations the rentalReservations to set
     */
    public void setRentalReservations(List<RentalReservation> rentalReservations) {
        this.rentalReservations = rentalReservations;
    }
    
}
