package no.ding.pk.web.dto.web.client;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    @JsonAlias("phone")
    private String phoneNumber;
    private String email;
    private Long salesRoleId;
    private String associatedPlace;

    /**
     * Fullmaktsnivå Vanlig Avfall
     */
    private Integer powerOfAttorneyOA;

    /**
     * Fullmaktsnivå Farlig Avfall
     */
    private Integer powerOfAttorneyFA;

    /**
     * Overordnet fullmaktsinnehaver (salgssjef)
     */
    private String overallPowerOfAttorney;
    private String emailSalesManager;
    private String regionalManagersPowerOfAttorney;
    private String emailRegionalManager;
    private String department;

    @Override
    public String toString() {
        return "UserDTO [adId=" + adId + ", associatedPlace=" + associatedPlace + ", department=" + department
                + ", email=" + email + ", emailRegionalManager=" + emailRegionalManager + ", emailSalesManager="
                + emailSalesManager + ", fullName=" + fullName + ", id=" + id + ", jobTitle=" + jobTitle + ", name="
                + name + ", orgName=" + orgName + ", orgNr=" + orgNr + ", overallPowerOfAttorney="
                + overallPowerOfAttorney + ", phoneNumber=" + phoneNumber + ", powerOfAttorneyFA=" + powerOfAttorneyFA
                + ", powerOfAttorneyOA=" + powerOfAttorneyOA + ", regionName=" + regionName
                + ", regionalManagersPowerOfAttorney=" + regionalManagersPowerOfAttorney + ", resourceNr=" + resourceNr
                + ", salesRoleId=" + salesRoleId + ", sureName=" + sureName + ", username=" + username + ", usernameAlias="
                + usernameAlias + "]";
    }
}
    