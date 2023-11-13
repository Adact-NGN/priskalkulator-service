package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder(builderMethodName = "hiddenBuilder")
@Data
public class ConditionRecordValidityItemDTO {
    @JsonProperty(value = "ConditionRecord")
    private String conditionRecord;

    @JsonProperty(value = "SalesOrganization", required = true)
    private String salesOrganization;

    @JsonProperty(value = "SalesOffice", required = true)
    private String salesOffice;

    @JsonProperty(value = "Customer")
    private String customer;

    @JsonProperty(value = "CustomerHierarchy")
    private String customerHierarchy; // Node number

    @JsonProperty(value = "Material", required = true)
    private String material;

    @JsonProperty(value = "CustomerConditionGroup")
    private String zone;

    @JsonIgnore
    private String deviceType;

    public static ConditionRecordValidityItemDTOBuilder builder(String salesOrganization, String salesOffice, String material) {
        return hiddenBuilder().salesOrganization(salesOrganization).salesOffice(salesOffice).material(material);
    }
}
