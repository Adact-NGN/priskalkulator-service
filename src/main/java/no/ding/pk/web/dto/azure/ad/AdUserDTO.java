package no.ding.pk.web.dto.azure.ad;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = { "@odata.context" })
public class AdUserDTO {
    /**
     * Azure AD id field.
     */
    @JsonProperty("id")
    @JsonAlias("adId")
    private String adId;

    @JsonProperty("surename")
    private String sureName;

    @JsonProperty("givenName")
    private String name;

    @JsonProperty("displayName")
    private String fullName;

    @JsonProperty("employeeId")
    private String resourceNr;

    @JsonProperty("userPrincipalName")
    private String username;

    @JsonProperty("mailNickname")
    private String usernameAlias;

    @JsonProperty("mobilePhone")
    private String phoneNumber;

    @JsonProperty("mail")
    private String email;

    @JsonProperty(value = "salesRole", required = false)
    private String salesRole;

    @JsonProperty("city")
    private String associatedPlace;

    @JsonProperty("jobTitle")
    private String jobTitle;

    @JsonProperty("department")
    private String department;

    /**
     * Fullmaktsnivå Vanlig Avfall
     */
    @JsonProperty(required = false)
    private Integer powerOfAttorneyOA;

    /**
     * Fullmaktsnivå Farlig Avfall
     */
    @JsonProperty(required = false)
    private Integer powerOfAttorneyFA;

    /**
     * Overordnet fullmaktsinnehaver (salgssjef)
     */
    @JsonProperty(required = false)
    private String overallPowerOfAttorney;

    @JsonProperty(required = false)
    private String emailSalesManager;

    @JsonProperty(required = false)
    private String regionalManagersPowerOfAttorney;

    @JsonProperty(required = false)
    private String emailRegionalManager;

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
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

    public String getSalesRole() {
        return salesRole;
    }

    public void setSalesRole(String salesRole) {
        this.salesRole = salesRole;
    }

    public String getAssociatedPlace() {
        return associatedPlace;
    }

    public void setAssociatedPlace(String associatedPlace) {
        this.associatedPlace = associatedPlace;
    }

    public Integer getPowerOfAttorneyOA() {
        return powerOfAttorneyOA;
    }

    public void setPowerOfAttorneyOA(Integer powerOfAttorneyOA) {
        this.powerOfAttorneyOA = powerOfAttorneyOA;
    }

    public Integer getPowerOfAttorneyFA() {
        return powerOfAttorneyFA;
    }

    public void setPowerOfAttorneyFA(Integer powerOfAttorneyFA) {
        this.powerOfAttorneyFA = powerOfAttorneyFA;
    }

    public String getOverallPowerOfAttorney() {
        return overallPowerOfAttorney;
    }

    public void setOverallPowerOfAttorney(String overallPowerOfAttorney) {
        this.overallPowerOfAttorney = overallPowerOfAttorney;
    }

    public String getEmailSalesManager() {
        return emailSalesManager;
    }

    public void setEmailSalesManager(String emailSalesManager) {
        this.emailSalesManager = emailSalesManager;
    }

    public String getRegionalManagersPowerOfAttorney() {
        return regionalManagersPowerOfAttorney;
    }

    public void setRegionalManagersPowerOfAttorney(String regionalManagersPowerOfAttorney) {
        this.regionalManagersPowerOfAttorney = regionalManagersPowerOfAttorney;
    }

    public String getEmailRegionalManager() {
        return emailRegionalManager;
    }

    public void setEmailRegionalManager(String emailRegionalManager) {
        this.emailRegionalManager = emailRegionalManager;
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

    
}
