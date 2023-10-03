package no.ding.pk.web.dto.v2.web.client.offer.patch;

import no.ding.pk.web.dto.v1.web.client.offer.CustomerTermsDTO;
import no.ding.pk.web.dto.v2.web.client.offer.SalesOfficeDTO;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Date;
import java.util.List;

public class PriceOfferUpdateDto {
    private JsonNullable<String> priceOfferStatus;
    private JsonNullable<String> materialsForApproval;
    private JsonNullable<String> customerNumber;
    private JsonNullable<String> customerName;
    private JsonNullable<String> customerType;
    private JsonNullable<String> streetAddress;
    private JsonNullable<String> postalNumber;
    private JsonNullable<String> city;
    private JsonNullable<String> organizationNumber;
    private JsonNullable<Date> activationDate;
    private JsonNullable<Date> approvalDate;
    private JsonNullable<Date> dateIssued;

    private JsonNullable<Long> salesEmployeeId;
    private JsonNullable<Long> approverId;

    private JsonNullable<Boolean> needsApproval;
    private JsonNullable<String> additionalInformation;
    private JsonNullable<String> generalComment;

    private JsonNullable<CustomerTermsDTO> customerTerms;

    private JsonNullable<List<ContactPersonUpdateDto>> contactPersonList;
    private JsonNullable<List<SalesOfficeDTO>> salesOfficeList;

    private JsonNullable<String> createdBy;
    private JsonNullable<String> lastModifiedBy;

    public JsonNullable<String> getPriceOfferStatus() {
        return priceOfferStatus;
    }

    public void setPriceOfferStatus(JsonNullable<String> priceOfferStatus) {
        this.priceOfferStatus = priceOfferStatus;
    }

    public JsonNullable<String> getMaterialsForApproval() {
        return materialsForApproval;
    }

    public void setMaterialsForApproval(JsonNullable<String> materialsForApproval) {
        this.materialsForApproval = materialsForApproval;
    }

    public JsonNullable<String> getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(JsonNullable<String> customerNumber) {
        this.customerNumber = customerNumber;
    }

    public JsonNullable<String> getCustomerName() {
        return customerName;
    }

    public void setCustomerName(JsonNullable<String> customerName) {
        this.customerName = customerName;
    }

    public JsonNullable<String> getCustomerType() {
        return customerType;
    }

    public void setCustomerType(JsonNullable<String> customerType) {
        this.customerType = customerType;
    }

    public JsonNullable<String> getPostalNumber() {
        return postalNumber;
    }

    public void setPostalNumber(JsonNullable<String> postalNumber) {
        this.postalNumber = postalNumber;
    }

    public JsonNullable<String> getCity() {
        return city;
    }

    public void setCity(JsonNullable<String> city) {
        this.city = city;
    }

    public JsonNullable<String> getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(JsonNullable<String> organizationNumber) {
        this.organizationNumber = organizationNumber;
    }

    public JsonNullable<Date> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(JsonNullable<Date> activationDate) {
        this.activationDate = activationDate;
    }

    public JsonNullable<Date> getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(JsonNullable<Date> approvalDate) {
        this.approvalDate = approvalDate;
    }

    public JsonNullable<Date> getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(JsonNullable<Date> dateIssued) {
        this.dateIssued = dateIssued;
    }

    public JsonNullable<Long> getSalesEmployeeId() {
        return salesEmployeeId;
    }

    public void setSalesEmployeeId(JsonNullable<Long> salesEmployeeId) {
        this.salesEmployeeId = salesEmployeeId;
    }

    public JsonNullable<Long> getApproverId() {
        return approverId;
    }

    public void setApproverId(JsonNullable<Long> approverId) {
        this.approverId = approverId;
    }

    public JsonNullable<Boolean> getNeedsApproval() {
        return needsApproval;
    }

    public void setNeedsApproval(JsonNullable<Boolean> needsApproval) {
        this.needsApproval = needsApproval;
    }

    public JsonNullable<String> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(JsonNullable<String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public JsonNullable<String> getGeneralComment() {
        return generalComment;
    }

    public void setGeneralComment(JsonNullable<String> generalComment) {
        this.generalComment = generalComment;
    }

    public JsonNullable<CustomerTermsDTO> getCustomerTerms() {
        return customerTerms;
    }

    public void setCustomerTerms(JsonNullable<CustomerTermsDTO> customerTerms) {
        this.customerTerms = customerTerms;
    }

    public JsonNullable<List<ContactPersonUpdateDto>> getContactPersonList() {
        return contactPersonList;
    }

    public void setContactPersonList(JsonNullable<List<ContactPersonUpdateDto>> contactPersonList) {
        this.contactPersonList = contactPersonList;
    }

    public JsonNullable<List<SalesOfficeDTO>> getSalesOfficeList() {
        return salesOfficeList;
    }

    public void setSalesOfficeList(JsonNullable<List<SalesOfficeDTO>> salesOfficeList) {
        this.salesOfficeList = salesOfficeList;
    }

    public JsonNullable<String> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(JsonNullable<String> createdBy) {
        this.createdBy = createdBy;
    }

    public JsonNullable<String> getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(JsonNullable<String> lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public JsonNullable<String> getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(JsonNullable<String> streetAddress) {
        this.streetAddress = streetAddress;
    }
}
