package no.ding.pk.web.dto.sap;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class ContactPersonCustomerDTO {
    @JsonAlias("Customer")
    private String customerNumber;

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
}
