package no.ding.pk.web.dto.sap.pricing;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SapCreatePricingEntitiesResponse {
    private String salesOrg;
    private String salesOffice;
    private String materialNumber;
    private String deviceType;
    private String zone;
    private boolean isUpdated;
}
