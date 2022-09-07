package no.ding.pk.web.dto;

public class UserDTO {
    private Long id;
    private String adId;
    private String orgNr;
    private String orgName;
    private String regionName;
    private String sureName;
    private String name;
    private String username;
    private String usernameAlias;
    private String jobTitle;
    private String fullName;
    private String resourceNr;
    private String phoneNumber;
    private String email;
    private String salesRole;
    private String associatedPlace;

    /**
     * Fullmaktsnivå Vanlig Avfall
     */
    private Integer powerOfAtterneyOA;

    /**
     * Fullmaktsnivå Farlig Avfall
     */
    private Integer powerOfAtterneyFA;

    /**
     * Overordnet fullmaktsinnehaver (salgssjef)
     */
    private String overallPowerOfAtterney;
    private String emailSalesManager;
    private String regionalManagersPowerOfAtterney;
    private String emailRegionalManager;
    private String department;

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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsernameAlias() {
        return usernameAlias;
    }
    public void setUsernameAlias(String usernameAlias) {
        this.usernameAlias = usernameAlias;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    
}
    