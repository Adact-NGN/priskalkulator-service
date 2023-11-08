package no.ding.pk.web.dto.sap.pricing;

import lombok.Data;

import java.util.List;

@Data
public class SapCreatePricingEntitiesRequest {
    private Long priceOfferId;
    private String customerName;
    private String customerNumber;
    private List<PricingEntityCombinationMap> pricingEntityCombinationMaps;
}
