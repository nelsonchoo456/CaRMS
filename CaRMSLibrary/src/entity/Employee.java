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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import util.enumeration.UserRoleEnum;

/**
 *
 * @author Nelson Choo
 */
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private UserRoleEnum userRoleEnum;
    @Column(nullable = false, unique = true, length = 64)
    @NotNull
    @Size(max = 64)
    private String username;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String password;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Outlet outlet;
    
    @OneToMany(mappedBy = "driver")
    private List<DispatchRecord> dispatchRecords;

    public Employee() {
        this.dispatchRecords = new ArrayList<DispatchRecord>();
    }
    

    public Employee(String name, UserRoleEnum userRoleEnum, String username, String password) {
        this.name = name;
        this.userRoleEnum = userRoleEnum;
        this.username = username;
        this.password = password;
        this.dispatchRecords = new ArrayList<DispatchRecord>();
    }
    
    

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employee[ id=" + employeeId + " ]";
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
     * @return the userRoleEnum
     */
    public UserRoleEnum getUserRoleEnum() {
        return userRoleEnum;
    }

    /**
     * @param userRoleEnum the userRoleEnum to set
     */
    public void setUserRoleEnum(UserRoleEnum userRoleEnum) {
        this.userRoleEnum = userRoleEnum;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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
     * @return the dispatchRecords
     */
    @XmlTransient
    public List<DispatchRecord> getDispatchRecords() {
        return dispatchRecords;
    }

    /**
     * @param dispatchRecords the dispatchRecords to set
     */
    public void setDispatchRecords(List<DispatchRecord> dispatchRecords) {
        this.dispatchRecords = dispatchRecords;
    }
    
}
