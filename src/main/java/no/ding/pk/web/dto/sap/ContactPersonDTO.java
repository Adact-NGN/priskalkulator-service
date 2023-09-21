package no.ding.pk.web.dto.sap;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContactPersonDTO {
    @JsonAlias("Customer")
    private String customer;
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
    private Date changedDate;
    @JsonAlias("ChangedTime") //: "21:28:44",
    private String changedTime;
    @JsonAlias("_Customers")
    private List<ContactPersonCustomerDTO> customers;

    @JsonAlias("RelationshipNumber")
    private String relationshipNumber;

    @JsonAlias("ValidityEndDate")
    private Date validityEndDate;
}
    