package no.ding.pk.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class SalesOrgDTO {

    @JsonAlias("SalesOrganization")
    private String salesOrganization;

    @JsonAlias("PostalNumber")
    private String postalNumber;

    @JsonAlias("City")
    private String city;

    @JsonAlias("SalesOffice")
    private String salesOffice;

    @JsonAlias("SalesZone")
    private String salesZone;

    public String getSalesOrganization() {
        return salesOrganization;
    }

    public void setSalesOrganization(String salesOrganization) {
        this.salesOrganization = salesOrganization;
    }

    public String getPostalNumber() {
        return postalNumber;
    }

    public void setPostalNumber(String postalNumber) {
        this.postalNumber = postalNumber;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((salesOrganization == null) ? 0 : salesOrganization.hashCode());
        result = prime * result + ((postalNumber == null) ? 0 : postalNumber.hashCode());
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
        if (postalNumber == null) {
            if (other.postalNumber != null)
                return false;
        } else if (!postalNumber.equals(other.postalNumber))
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
