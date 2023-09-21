package no.ding.pk.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@NamedEntityGraph(name = "User.salesRole", attributeNodes = @NamedAttributeNode("salesRole"))
@Table(name = "users")
public class User extends Auditable implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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