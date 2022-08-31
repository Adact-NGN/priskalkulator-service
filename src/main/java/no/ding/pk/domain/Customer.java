package no.ding.pk.domain;

public class Customer {
    private String businessPartner; // "BusinessPartner": "21",
    private String salesOrganization;                // "Salgsorganiasjon": "100",
    private String name1; // "Navn1": "Dekkmann",
    private String name2; // "Navn2": "",
    private String dateOfBirth; // "Fodselsdato": "",
    private String motherCompany; // "Morselskap": "",
    private String organizationNumber; // "Orgnummer": "",
    private String customerGroup; // "Kundegruppe": "Organisasjon",
    private String invoiceType; // "FakturaType": "",
    private String phoneNumber; // "Telefon": "",
    private String mobileNumber; // "Mobil": "",
    private String email; // "Epost": "",

   
    public Customer() {
    }


    public String getBusinessPartner() {
        return businessPartner;
    }
    public void setBusinessPartner(String businessPartner) {
        this.businessPartner = businessPartner;
    }
    public String getSalesOrganization() {
        return salesOrganization;
    }
    public void setSalesOrganization(String salesOrganization) {
        this.salesOrganization = salesOrganization;
    }
    public String getName1() {
        return name1;
    }
    public void setName1(String name1) {
        this.name1 = name1;
    }
    public String getName2() {
        return name2;
    }
    public void setName2(String name2) {
        this.name2 = name2;
    }
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getMotherCompany() {
        return motherCompany;
    }
    public void setMotherCompany(String motherCompany) {
        this.motherCompany = motherCompany;
    }
    public String getOrganizationNumber() {
        return organizationNumber;
    }
    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }
    public String getCustomerGroup() {
        return customerGroup;
    }
    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }
    public String getInvoiceType() {
        return invoiceType;
    }
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((businessPartner == null) ? 0 : businessPartner.hashCode());
        result = prime * result + ((customerGroup == null) ? 0 : customerGroup.hashCode());
        result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((mobileNumber == null) ? 0 : mobileNumber.hashCode());
        result = prime * result + ((motherCompany == null) ? 0 : motherCompany.hashCode());
        result = prime * result + ((name1 == null) ? 0 : name1.hashCode());
        result = prime * result + ((name2 == null) ? 0 : name2.hashCode());
        result = prime * result + ((organizationNumber == null) ? 0 : organizationNumber.hashCode());
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
        result = prime * result + ((salesOrganization == null) ? 0 : salesOrganization.hashCode());
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
        Customer other = (Customer) obj;
        if (businessPartner == null) {
            if (other.businessPartner != null)
                return false;
        } else if (!businessPartner.equals(other.businessPartner))
            return false;
        if (customerGroup == null) {
            if (other.customerGroup != null)
                return false;
        } else if (!customerGroup.equals(other.customerGroup))
            return false;
        if (dateOfBirth == null) {
            if (other.dateOfBirth != null)
                return false;
        } else if (!dateOfBirth.equals(other.dateOfBirth))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (mobileNumber == null) {
            if (other.mobileNumber != null)
                return false;
        } else if (!mobileNumber.equals(other.mobileNumber))
            return false;
        if (motherCompany == null) {
            if (other.motherCompany != null)
                return false;
        } else if (!motherCompany.equals(other.motherCompany))
            return false;
        if (name1 == null) {
            if (other.name1 != null)
                return false;
        } else if (!name1.equals(other.name1))
            return false;
        if (name2 == null) {
            if (other.name2 != null)
                return false;
        } else if (!name2.equals(other.name2))
            return false;
        if (organizationNumber == null) {
            if (other.organizationNumber != null)
                return false;
        } else if (!organizationNumber.equals(other.organizationNumber))
            return false;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        if (salesOrganization == null) {
            if (other.salesOrganization != null)
                return false;
        } else if (!salesOrganization.equals(other.salesOrganization))
            return false;
        return true;
    }

    
    
}
