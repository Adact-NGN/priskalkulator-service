package no.ding.pk.web.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ContactPersonDTO {
    @JsonAlias("ContactPerson") //: "200039946",
    private String contactPerson;
    @JsonAlias("FirstName") //: "",
    private String firstName;
    @JsonAlias("LastName") //: "Hanne Karin Varhaug Søberg",
    private String lastName;
    @JsonAlias("EmailAddress") //: "",
    private String emailAddress;
    @JsonAlias({"MobileNumber", "MobilePhoneNumber"})  //: "",
    private String mobileNumber;
    @JsonAlias("PhoneNumber") //: "",
    private String phoneNumber;
    @JsonAlias("ContactPersonFunction") //: "02",
    private String contactPersonFunction;
    @JsonAlias("ContactPersonFunctionName") //: "Finans Ansvarlig",
    private String contactPersonFunctionName;
    @JsonAlias("ContactPersonDepartment") //: "",
    private String contactPersonDepartment;
    @JsonAlias("ContactPersonDepartmentName") //: "",
    private String contactPersonDepartmentName;
    @JsonAlias("ChangedDate") //: "2021-09-09",
    private Date chagedDate;
    @JsonAlias("ChangedTime") //: "21:28:44",
    private String chagedTime;
    @JsonAlias("_Customers")
    private List<ContactPersonCustomerDTO> customers;

    // [
    //     {
    //         "Customer": "126094"
    //     }
    // ]

    public String getContactPerson() {
        return contactPerson;
    }
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getContactPersonFunction() {
        return contactPersonFunction;
    }
    public void setContactPersonFunction(String contactPersonFunction) {
        this.contactPersonFunction = contactPersonFunction;
    }
    public String getContactPersonFunctionName() {
        return contactPersonFunctionName;
    }
    public void setContactPersonFunctionName(String contactPersonFunctionName) {
        this.contactPersonFunctionName = contactPersonFunctionName;
    }
    public String getContactPersonDepartment() {
        return contactPersonDepartment;
    }
    public void setContactPersonDepartment(String contactPersonDepartment) {
        this.contactPersonDepartment = contactPersonDepartment;
    }
    public String getContactPersonDepartmentName() {
        return contactPersonDepartmentName;
    }
    public void setContactPersonDepartmentName(String contactPersonDepartmentName) {
        this.contactPersonDepartmentName = contactPersonDepartmentName;
    }
    public Date getChagedDate() {
        return chagedDate;
    }
    public void setChagedDate(Date chagedDate) {
        this.chagedDate = chagedDate;
    }
    public String getChagedTime() {
        return chagedTime;
    }
    public void setChagedTime(String chagedTime) {
        this.chagedTime = chagedTime;
    }
    public List<ContactPersonCustomerDTO> getCustomers() {
        return customers;
    }
    public void setCustomers(List<ContactPersonCustomerDTO> customers) {
        this.customers = customers;
    }
        
           
}
    