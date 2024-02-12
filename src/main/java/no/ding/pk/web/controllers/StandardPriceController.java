package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.service.sap.StandardPriceService;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "StandardPriceController", description = "Controller for getting material standard price from SAP.")
@RestController
@RequestMapping(value = {"/api/standard-price", "/api/v1/standard-price"})
public class StandardPriceController {

    private final Logger log = LoggerFactory.getLogger(StandardPriceController.class);
    
    private final StandardPriceService service;
    
    @Autowired
    public StandardPriceController(StandardPriceService priceService) {
        this.service = priceService;
    }
    
    /**
     * Get a list of Materials with standard price from SAP. Prices is fetch with the combination of Sales Office and Sales Organization.
     * @param salesOrg The sales organization to get the prices for.
     * @param salesOffice The sales office to get the prices for.
     * @param zone Set the zone number for only getting Material prices for a specific zone.
     * @return list of MaterialStdPriceDTO objects
     */
    @Operation(
            summary = "MaterialStdPrice - Get list of standard prices",
            description = "Get a list of Materials with standard price from SAP. Prices is fetch with the combination of Sales Office and Sales Organization.",
            method = "GET",
            parameters = {
                @Parameter(name = "salesorg", description = "Sales organization to get prices for.", required = true),
                @Parameter(name = "salesoffice", description = "Sales office to get prices for.", required = true),
                @Parameter(name = "zone", description = "Set the zone number to only get material prices for the given zone.."),
            },
            tags = "StandardPriceController"
    )
    @GetMapping(value = "/{salesorg}/{salesoffice}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(
            @PathVariable("salesorg") String salesOrg,
            @PathVariable("salesoffice") String salesOffice,
            @RequestParam(value = "zone", required = false) String zone
    ) {
        List<MaterialStdPriceDTO> materialList = service.getStdPricesForSalesOfficeAndSalesOrg(salesOrg, salesOffice, zone);

        log.debug(String.format("Amount returning: %d", materialList.size()));
        return materialList;
    }

    @Operation(
            summary = "MaterialStdPrice - Get list of standard prices for a material.",
            description = "Get a list of Materials with standard price from SAP. Prices is fetch with the combination of Sales Office, Sales Organization and material.",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesorg", description = "Sales organization to get prices for.", required = true),
                    @Parameter(name = "salesoffice", description = "Sales office to get prices for.", required = true),
                    @Parameter(name = "material", description = "Material number to get prices for.", required = true),
                    @Parameter(name = "zone", description = "Set the zone number to only get material prices for the given zone.."),
            },
            tags = "StandardPriceController"
    )
    @GetMapping(value = "/{salesorg}/{salesoffice}/{material}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialStdPriceDTO> getStdPriceForSalesOrgAndSalesOfficeAndMaterial(
            @PathVariable("salesorg") String salesOrg,
            @PathVariable("salesoffice") String salesOffice,
            @PathVariable("material") String material,
            @RequestParam(value = "zone", required = false) String zone
    ) {
        return service.getStandardPriceForSalesOrgSalesOfficeAndMaterial(salesOrg, salesOffice, material, zone);
    }
}
