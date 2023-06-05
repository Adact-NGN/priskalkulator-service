package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

// Price Condition Record Validity
@Data
public class SlsPrcgCndnRecdValidityDTO {
    @JsonProperty("results")
    private List<ValidCustomerMaterialDTO> results;
}
