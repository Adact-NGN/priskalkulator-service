package no.ding.pk.web.dto.sap;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Data
@JsonIgnoreProperties(value = { "AbcKlassifisering" })
public class CustomerDTO {
    @JsonAlias("Kundenummer")
    private String customerNumber;
    @JsonAlias("Selskap") //: "100",
    private String company;
    @JsonAlias("Distribusjonskanal") //: "01",
    private String distributionChannel;
    @JsonAlias("Division")
    private String division;
    @JsonAlias("CustomerName")
    private String customerName;
    @JsonAlias("BusinessPartnerCategory")
    private String businessPartnerCategory;
    @JsonAlias("Navn1") //: "Dekkmann Hedmark/Oppland",
    private String name1;
    @JsonAlias("Navn2") //: "",
    private String name2;
    @JsonAlias("Orgnummer") //: "",
    private String organizationNumber;
    
    @JsonAlias("Kundegruppe") //: "Organisasjon",
    private String customerGroup;
    @JsonAlias("Kundetype") //: "Node",
    private String customerType;
    @JsonAlias("Morselskap") //: "",
    private String motherCompany;
    @JsonAlias("Betaler") //: "",
    private String payer;
    @JsonAlias("Gate") //: "",
    private String streetAddress;
    @JsonAlias("Husnummer") //: "",
    private String houseNumber;
    @JsonAlias("Postnummer") //: "",
    private String postalNumber;
    @JsonAlias("Sted") //: "Oslo",
    private String city;
    @JsonAlias("Land") //: "NO",
    private String country;
    @JsonAlias("Region") //: "",
    private String region;
    @JsonAlias("Tidssone") //: "CET",
    private String timeZone;
    @JsonAlias("RegStrGrp") //: "",
    private String regStrGrp;
    @JsonAlias("Postboks") //: "",
    private String postBox;
    @JsonAlias("Postboksnummer") //: "",
    private String postBoxNumber;
    @JsonAlias("FirmaPostnummer") //: "",
    private String firmPostalNumber;
    @JsonAlias("CreditScore") //: "",
    private String creditScore;
    @JsonAlias("ChangedDate") //: null,
    private Date changedDate;
    @JsonAlias("ChangedTime") //: "00:00:00",
    private String changedTime;

    @JsonAlias("AbcKlassifisering")
    private String abcClassification;

    @JsonAlias("PermissionHazardousWaste")
    private String permissionHazardousWaste;

    @JsonAlias("IsConstructionCustomer")
    private String isConstructionCustomer;

    @JsonAlias("HasCentralAgreement")
    private String hasCentralAgreement;

    @JsonAlias("KontaktPersoner") //: []
    private List<ContactPersonDTO> contactPersons;

    @JsonAlias("KundeBransje")
    private List<CustomerBranchDTO> customerBranchList;

    @JsonAlias("Nodekunder")
    private List<NodeCustomerDTO> nodeCustomerList;
}
