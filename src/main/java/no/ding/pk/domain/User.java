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

    private String adId;

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
    private String username;

    @Column
    private String usernameAlias;

    @Column
    private String jobTitle;

    @Column
    private String fullName;

    @Column
    private String resourceNr;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @ManyToOne(optional = true)
    private SalesRole salesRole;

    @Column
    private String associatedPlace;

    /**
     * Fullmaktsnivå Vanlig Avfall
     */
    @Column
    private Integer powerOfAtterneyOA;

    /**
     * Fullmaktsnivå Farlig Avfall
     */
    @Column
    private Integer powerOfAtterneyFA;

    /**
     * Overordnet fullmaktsinnehaver (salgssjef)
     */
    @Column
    private String overallPowerOfAtterney;

    @Column
    private String emailSalesManager;

    @Column
    private String regionalManagersPowerOfAtterney;

    @Column
    private String emailRegionalManager;

    @Column
    private String department;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getUsernameAlias() {
        return usernameAlias;
    }

    public void setUsernameAlias(String usernameAlias) {
        this.usernameAlias = usernameAlias;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public User() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
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

    public Integer getPowerOfAtterneyOA() {
        return powerOfAtterneyOA;
    }

    public void setPowerOfAtterneyOA(Integer powerOfAtterneyOA) {
        this.powerOfAtterneyOA = powerOfAtterneyOA;
    }

    public Integer getPowerOfAtterneyFA() {
        return powerOfAtterneyFA;
    }

    public void setPowerOfAtterneyFA(Integer powerOfAtterneyFA) {
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
        return "User [adId=" + adId + ", associatedPlace=" + associatedPlace + ", email=" + email
                + ", emailRegionalManager=" + emailRegionalManager + ", emailSalesManager=" + emailSalesManager
                + ", fullName=" + fullName + ", id=" + id + ", name=" + name + ", orgName=" + orgName + ", orgNr="
                + orgNr + ", overallPowerOfAtterney=" + overallPowerOfAtterney + ", phoneNumber=" + phoneNumber
                + ", powerOfAtterneyFA=" + powerOfAtterneyFA + ", powerOfAtterneyOA=" + powerOfAtterneyOA
                + ", regionName=" + regionName + ", regionalManagersPowerOfAtterney=" + regionalManagersPowerOfAtterney
                + ", resourceNr=" + resourceNr + ", salesRole=" + salesRole + ", sureName=" + sureName + ", username="
                + username + ", usernameAlias=" + usernameAlias + "]";
    }

    
}