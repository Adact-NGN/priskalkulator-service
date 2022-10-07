package no.ding.pk.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactPersonCustomerDTO {
    @JsonProperty("Customer")
    private String customerNumber;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
}
