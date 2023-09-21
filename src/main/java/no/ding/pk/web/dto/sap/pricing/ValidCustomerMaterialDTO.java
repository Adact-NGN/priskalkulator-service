package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ValidCustomerMaterialDTO {
    @JsonProperty("SalesOrganization")
    private String salesOrganization;
    @JsonProperty("Customer")
    private String customer;
    @JsonProperty("Material")
    private String material;
    @JsonProperty("SalesOffice")
    private String salesOffice;
    @JsonProperty("CustomerConditionGroup")
    private String customerConditionGroup;
}
