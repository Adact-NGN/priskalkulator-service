package no.ding.pk.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ContactPersonCustomerDTO {
    @JsonAlias("Customer")
    private String customerNumber;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
}
