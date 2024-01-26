package no.ding.pk.web.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.service.sap.SapPricingService;
import no.ding.pk.web.dto.sap.pricing.SapCreatePricingEntitiesRequest;
import no.ding.pk.web.dto.sap.pricing.SapCreatePricingEntitiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "SapCreatePricingEntitiesController", description = "Controller for creating and sending new customer prices to SAP.")
@RestController
@RequestMapping("/api/v1/sap-pricing")
public class SapCreatePricingEntitiesController {

    private static final Logger log = LoggerFactory.getLogger(SapCreatePricingEntitiesRequest.class);

    private final SapPricingService sapPricingService;

    @Autowired
    public SapCreatePricingEntitiesController(SapPricingService sapPricingService) {
        this.sapPricingService = sapPricingService;
    }

    @Operation(description = "Create new material pricing for customer and sales office in SAP",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "SapCreatePricingEntitiesRequest", required = true),
            tags = "SapCreatePricingEntitiesController"
    )
    @PostMapping(path = "/create")
    public List<SapCreatePricingEntitiesResponse> createNewSapPricingEntities(@RequestBody SapCreatePricingEntitiesRequest pricingEntitiesRequest) {
        if(pricingEntitiesRequest.getPriceOfferId() == null) {
            log.debug("No price offer id provided");
            throw new RuntimeException("No price offer id provided");
        }

        return sapPricingService.updateMaterialPriceEntities(
                pricingEntitiesRequest.getPriceOfferId(),
                pricingEntitiesRequest.getCustomerNumber(),
                pricingEntitiesRequest.getNodeNumber(),
                pricingEntitiesRequest.getCustomerName(),
                pricingEntitiesRequest.getPricingEntityCombinationMaps());
    }

    @Operation(description = "Batch operation for creating new material pricing for customer and sales office in SAP",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "SapCreatePricingEntitiesRequest", required = true),
            tags = "SapCreatePricingEntitiesController"
    )
    @PostMapping(path = "/create/batch")
    public List<SapCreatePricingEntitiesResponse> createNewSapPricingEntitiesBatchOperation(@RequestBody SapCreatePricingEntitiesRequest pricingEntitiesRequest) {
        if(pricingEntitiesRequest.getPriceOfferId() == null) {
            log.debug("No price offer id provided");
            throw new RuntimeException("No price offer id provided");
        }

        return sapPricingService.batchUpdateMaterialPriceEntities(pricingEntitiesRequest.getPriceOfferId(),
                pricingEntitiesRequest.getCustomerNumber(),
                pricingEntitiesRequest.getNodeNumber(),
                pricingEntitiesRequest.getCustomerName(),
                pricingEntitiesRequest.getPricingEntityCombinationMaps());
    }
}
