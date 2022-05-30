package no.ding.pk.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String orgNr;

    @Column
    private String orgName;

    @Column
    private String regionName;

    @Column
    private String sureName;

    @Column
    private String name;

    @Column
    private String fullName;

    @Column
    private String resourceNr;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @ManyToOne
    private SalesRole salesRole;

    @Column
    private String associatedPlace;

    @Column
    private String powerOfAtterneyOA;

    @Column
    private String powerOfAtterneyFA;

    @Column
    private String overallPowerOfAtterney;

    @Column
    private String emailSalesManager;

    @Column
    private String regionalManagersPowerOfAtterney;

    @Column
    private String emailRegionalManager;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgNr() {
        return orgNr;
    }

    public void setOrgNr(String orgNr) {
        this.orgNr = orgNr;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getResourceNr() {
        return resourceNr;
    }

    public void setResourceNr(String resourceNr) {
        this.resourceNr = resourceNr;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SalesRole getSalesRole() {
        return salesRole;
    }

    public void setSalesRole(SalesRole salesRole) {
        this.salesRole = salesRole;
    }

    public String getAssociatedPlace() {
        return associatedPlace;
    }

    public void setAssociatedPlace(String associatedPlace) {
        this.associatedPlace = associatedPlace;
    }

    public String getPowerOfAtterneyOA() {
        return powerOfAtterneyOA;
    }

    public void setPowerOfAtterneyOA(String powerOfAtterneyOA) {
        this.powerOfAtterneyOA = powerOfAtterneyOA;
    }

    public String getPowerOfAtterneyFA() {
        return powerOfAtterneyFA;
    }

    public void setPowerOfAtterneyFA(String powerOfAtterneyFA) {
        this.powerOfAtterneyFA = powerOfAtterneyFA;
    }

    public String getOverallPowerOfAtterney() {
        return overallPowerOfAtterney;
    }

    public void setOverallPowerOfAtterney(String overallPowerOfAtterney) {
        this.overallPowerOfAtterney = overallPowerOfAtterney;
    }

    public String getEmailSalesManager() {
        return emailSalesManager;
    }

    public void setEmailSalesManager(String emailSalesManager) {
        this.emailSalesManager = emailSalesManager;
    }

    public String getRegionalManagersPowerOfAtterney() {
        return regionalManagersPowerOfAtterney;
    }

    public void setRegionalManagersPowerOfAtterney(String regionalManagersPowerOfAtterney) {
        this.regionalManagersPowerOfAtterney = regionalManagersPowerOfAtterney;
    }

    public String getEmailRegionalManager() {
        return emailRegionalManager;
    }

    public void setEmailRegionalManager(String emailRegionalManager) {
        this.emailRegionalManager = emailRegionalManager;
    }

    @Override
    public String toString() {
        return "User [associatedPlace=" + associatedPlace + ", email=" + email + ", emailRegionalManager="
                + emailRegionalManager + ", emailSalesManager=" + emailSalesManager + ", fullName=" + fullName
                + ", name=" + name + ", orgName=" + orgName + ", orgNr=" + orgNr + ", overallPowerOfAtterney="
                + overallPowerOfAtterney + ", phoneNumber=" + phoneNumber + ", powerOfAtterneyFA=" + powerOfAtterneyFA
                + ", powerOfAtterneyOA=" + powerOfAtterneyOA + ", regionName=" + regionName
                + ", regionalManagersPowerOfAtterney=" + regionalManagersPowerOfAtterney + ", resourceNr=" + resourceNr
                + ", salesRole=" + salesRole + ", sureName=" + sureName + "]";
    }

    
    
}