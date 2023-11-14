package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.pricing.PricingEntityCombinationMap;
import no.ding.pk.web.dto.sap.pricing.SapCreatePricingEntitiesResponse;

import java.util.List;

public interface SapPricingService {
    List<SapCreatePricingEntitiesResponse> updateMaterialPriceEntities(Long priceOfferId, String customerNumber, String nodeNumber, String customerName, List<PricingEntityCombinationMap> pricingEntityCombinationMaps);

    List<SapCreatePricingEntitiesResponse> batchUpdateMaterialPriceEntities(Long priceOfferId, String customerNumber, String nodeNumber, String customerName, List<PricingEntityCombinationMap> pricingEntityCombinationMaps);
}
