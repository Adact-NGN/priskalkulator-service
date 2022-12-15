package no.ding.pk.web.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "AbcKlassifisering" })
public class CustomerDTO {
    @JsonAlias("Kundenummer")
    private String customerNumber;
    @JsonAlias("Selskap") //: "100",
    private String company;
    @JsonAlias("Distribusjonskanal") //: "01",
    private String distributionChannel;
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

    @JsonAlias("KontaktPersoner") //: []
    private List<ContactPersonDTO> contactPersons;

    @JsonAlias("KundeBransje")
    private List<CustomerBranchDTO> customerBranchList;

    @JsonAlias("Nodekunder")
    private List<NodeCustomerDTO> nodeCustomerList;
    
    public String getCustomerNumber() {
        return customerNumber;
    }
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getDistributionChannel() {
        return distributionChannel;
    }
    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
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
    public String getCustomerType() {
        return customerType;
    }
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    public String getMotherCompany() {
        return motherCompany;
    }
    public void setMotherCompany(String motherCompany) {
        this.motherCompany = motherCompany;
    }
    public String getPayer() {
        return payer;
    }
    public void setPayer(String payer) {
        this.payer = payer;
    }
    public String getStreetAddress() {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    public String getHouseNumber() {
        return houseNumber;
    }
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
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
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    public String getRegStrGrp() {
        return regStrGrp;
    }
    public void setRegStrGrp(String regStrGrp) {
        this.regStrGrp = regStrGrp;
    }
    public String getPostBox() {
        return postBox;
    }
    public void setPostBox(String postBox) {
        this.postBox = postBox;
    }
    public String getPostBoxNumber() {
        return postBoxNumber;
    }
    public void setPostBoxNumber(String postBoxNumber) {
        this.postBoxNumber = postBoxNumber;
    }
    public String getFirmPostalNumber() {
        return firmPostalNumber;
    }
    public void setFirmPostalNumber(String firmPostalNumber) {
        this.firmPostalNumber = firmPostalNumber;
    }
    public String getCreditScore() {
        return creditScore;
    }
    public void setCreditScore(String creditScore) {
        this.creditScore = creditScore;
    }
    public Date getChangedDate() {
        return changedDate;
    }
    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }
    public String getChangedTime() {
        return changedTime;
    }
    public void setChangedTime(String changedTime) {
        this.changedTime = changedTime;
    }
    public List<ContactPersonDTO> getContactPersons() {
        return contactPersons;
    }
    public void setContactPersons(List<ContactPersonDTO> contactPersons) {
        this.contactPersons = contactPersons;
    }
    public List<CustomerBranchDTO> getCustomerBranchList() {
        return customerBranchList;
    }
    public void setCustomerBranchList(List<CustomerBranchDTO> customerBranchList) {
        this.customerBranchList = customerBranchList;
    }
    public List<NodeCustomerDTO> getNodeCustomerList() {
        return nodeCustomerList;
    }
    public void setNodeCustomerList(List<NodeCustomerDTO> nodeCustomerList) {
        this.nodeCustomerList = nodeCustomerList;
    }
    public String getAbcClassification() {
        return abcClassification;
    }
    public void setAbcClassification(String abcClassification) {
        this.abcClassification = abcClassification;
    }
}
