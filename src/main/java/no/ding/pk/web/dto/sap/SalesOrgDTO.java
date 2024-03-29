package no.ding.pk.web.dto.sap;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class SalesOrgDTO {

    @JsonAlias("SalesOrganization")
    private String salesOrganization;

    @JsonAlias("SalesOffice")
    private String salesOffice;

    @JsonAlias("SalesOfficeName")
    private String salesOfficeName;

    @JsonAlias({"PostalNumber", "PostalCode"})
    private String postalCode;

    @JsonAlias("City")
    private String city;

    @JsonAlias("SalesZone")
    private String salesZone;

    @JsonAlias("AmountOfZones")
    private Integer amountOfZones;

    public String getSalesOrganization() {
        return salesOrganization;
    }

    public void setSalesOrganization(String salesOrganization) {
        this.salesOrganization = salesOrganization;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSalesOfficeName() {
        return salesOfficeName;
    }

    public void setSalesOfficeName(String salesOfficeName) {
        this.salesOfficeName = salesOfficeName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSalesOffice() {
        return salesOffice;
    }

    public void setSalesOffice(String salesOffice) {
        this.salesOffice = salesOffice;
    }

    public String getSalesZone() {
        return salesZone;
    }

    public void setSalesZone(String salesZone) {
        this.salesZone = salesZone;
    }

    public Integer getAmountOfZones() {
        return amountOfZones;
    }

    public void setAmountOfZones(Integer amountOfZones) {
        this.amountOfZones = amountOfZones;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((salesOrganization == null) ? 0 : salesOrganization.hashCode());
        result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
        result = prime * result + ((salesOfficeName == null) ? 0 : salesOfficeName.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((salesOffice == null) ? 0 : salesOffice.hashCode());
        result = prime * result + ((salesZone == null) ? 0 : salesZone.hashCode());
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
        SalesOrgDTO other = (SalesOrgDTO) obj;
        if (salesOrganization == null) {
            if (other.salesOrganization != null)
                return false;
        } else if (!salesOrganization.equals(other.salesOrganization))
            return false;
        if (postalCode == null) {
            if (other.postalCode != null)
                return false;
        } else if (!postalCode.equals(other.postalCode))
            return false;
        if(salesOfficeName == null) {
            if(other.salesOfficeName != null)
                return false;
        } else if(!salesOfficeName.equals(other.salesOfficeName))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (salesOffice == null) {
            if (other.salesOffice != null)
                return false;
        } else if (!salesOffice.equals(other.salesOffice))
            return false;
        if (salesZone == null) {
            if (other.salesZone != null)
                return false;
        } else if (!salesZone.equals(other.salesZone))
            return false;
        return true;
    }
}
