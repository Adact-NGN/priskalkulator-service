package no.ding.pk.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@NamedEntityGraph(name = "User.salesRole", attributeNodes = @NamedAttributeNode("salesRole"))
@Table(name = "users")
public class User implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
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
    
    @JsonAlias("phone")
    @Column()
    private String phoneNumber;
    
    @Column(unique = true)
    private String email;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "salesRole_id", foreignKey = @ForeignKey(name = "Fk_user_sales_role"))
    private SalesRole salesRole;
    
    @Column
    private String associatedPlace;
    
    /**
    * Fullmaktsnivå Vanlig Avfall
    */
    @Column
    private Integer powerOfAttorneyOA;
    
    /**
    * Fullmaktsnivå Farlig Avfall
    */
    @Column
    private Integer powerOfAttorneyFA;
    
    /**
    * Overordnet fullmaktsinnehaver (salgssjef)
    */
    @Column
    private String overallPowerOfAttorney;
    
    @Column
    private String emailSalesManager;
    
    @Column
    private String regionalManagersPowerOfAttorney;
    
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

        if(salesRole != null) {
            this.powerOfAttorneyOA = salesRole.getDefaultPowerOfAttorneyOa();
            this.powerOfAttorneyFA = salesRole.getDefaultPowerOfAttorneyFa();
        }
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
    
    @Override
    public String toString() {
        return "User [adId=" + adId + ", associatedPlace=" + associatedPlace + ", email=" + email
        + ", emailRegionalManager=" + emailRegionalManager + ", emailSalesManager=" + emailSalesManager
        + ", fullName=" + fullName + ", id=" + id + ", name=" + name + ", orgName=" + orgName + ", orgNr="
        + orgNr + ", overallPowerOfAttorney=" + overallPowerOfAttorney + ", phoneNumber=" + phoneNumber
        + ", powerOfAttorneyFA=" + powerOfAttorneyFA + ", powerOfAttorneyOA=" + powerOfAttorneyOA
        + ", regionName=" + regionName + ", regionalManagersPowerOfAttorney=" + regionalManagersPowerOfAttorney
        + ", resourceNr=" + resourceNr + ", salesRole=" + salesRole + ", sureName=" + sureName + ", username="
        + username + ", usernameAlias=" + usernameAlias + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adId == null) ? 0 : adId.hashCode());
        result = prime * result + ((orgNr == null) ? 0 : orgNr.hashCode());
        result = prime * result + ((orgName == null) ? 0 : orgName.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((usernameAlias == null) ? 0 : usernameAlias.hashCode());
        result = prime * result + ((resourceNr == null) ? 0 : resourceNr.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (adId == null) {
            if (other.adId != null)
                return false;
        } else if (!adId.equals(other.adId))
            return false;
        if (orgNr == null) {
            if (other.orgNr != null)
                return false;
        } else if (!orgNr.equals(other.orgNr))
            return false;
        if (orgName == null) {
            if (other.orgName != null)
                return false;
        } else if (!orgName.equals(other.orgName))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (usernameAlias == null) {
            if (other.usernameAlias != null)
                return false;
        } else if (!usernameAlias.equals(other.usernameAlias))
            return false;
        if (resourceNr == null) {
            if (other.resourceNr != null)
                return false;
        } else if (!resourceNr.equals(other.resourceNr))
        return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }
}