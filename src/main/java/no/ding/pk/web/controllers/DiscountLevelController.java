package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.service.DiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "DiscountLevelController", description = "Get discount levels for materials, sales organisations and sales offices.")
@RestController
@RequestMapping({"/api/discount/level", "/api/v1/discount/level"})
public class DiscountLevelController {

    private final static Logger log = LoggerFactory.getLogger(DiscountLevelController.class);

    private final DiscountService service;

    @Autowired
    public DiscountLevelController(DiscountService service) {
        this.service = service;
    }

    /**
     * Get discount level for a specific material which  belongs to a given material. The material resides under a sales organization and sales office.
     *
     * @param salesOrg       Sales organization number
     * @param salesOffice    Sales office number
     * @param materialNumber Material number
     * @param level          Discount level
     * @param zone           Zone to get discount for
     * @return A list with all the discount levels returned for the criteria given, else empty list.
     */
    @Operation(
            summary = "DiscountLevel - Get discount level for material",
            description = "Get discount level for a specific material which  belongs to a given material. The material resides under a sales organization and sales office.",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales organization number. E.g. 100", required = true),
                    @Parameter(name = "salesOffice", description = "Sales office number. E.g. 104", required = true),
                    @Parameter(name = "materialNumber", description = "Material number. E.g. 50103", required = true),
                    @Parameter(name = "level", description = "Discount level. E.g. 2"),
                    @Parameter(name = "zone", description = "Zone which the discount applies to. E.g. 3"),
            },
            tags = "DiscountLevelController"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscountLevel> getSpecificDiscountLevel(@RequestParam("salesOrg") String salesOrg,
                                                        @RequestParam("salesOffice") String salesOffice,
                                                        @RequestParam("materialNumber") String materialNumber,
                                                        @RequestParam(name = "level", required = false) Integer level,
                                                        @RequestParam(name = "zone", required = false) Integer zone) {
        log.debug("Getting discount level for: salesOrg: {}, salesOffice: {}, materialNumber: {} level: {}", salesOrg, salesOffice, materialNumber, level);
        return service.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, salesOffice, materialNumber, level, zone);
    }

    /**
     * Get a list of discount levels for a specific material or a list of material numbers.
     *
     * @param salesOrg       Sales organization number.
     * @param salesOffice    Sales office number.
     * @param materialNumbers Material number or a comma separated number list.
     * @param zone           Specify for which zone to get discount levels for.
     * @return A list of all the discount levels for one or multiple materials, else empty list.
     */
    @Operation(
            summary = "DiscountLevel - Get list of discounts for materials",
            description = "Get a list of discount levels for a specific material or a list of material numbers.",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales organization number. E.g. 100", required = true),
                    @Parameter(name = "salesOffice", description = "Sales office number. E.g. 104", required = true),
                    @Parameter(name = "materialNumbers", description = "Material number or a comma separated number list. E.g. 50103", required = true),
                    @Parameter(name = "zone", description = "Zone which the discount applies to. E.g. 3"),
            },
            tags = "DiscountLevelController"
    )
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscountLevel> getAllDiscountLevelsForSpecificDiscount(@RequestParam("salesOrg") String salesOrg,
                                                                       @RequestParam("salesOffice") String salesOffice,
                                                                       @RequestParam("materialNumbers") String materialNumbers,
                                                                       @RequestParam(value = "zone", required = false) String zone) {
        log.debug(String.format("Getting all discount levels for: salesOrg: %s materialNumber: %s", salesOrg, materialNumbers));
        return service.findAllDiscountLevelsForDiscountBySalesOrgAndSalesOfficeAndMaterialNumber(salesOrg, salesOffice, materialNumbers, zone);
    }

    /**
     * Update existing DiscountLevel object.
     * @param id The object id to look up the object with.
     * @param discountLevel New values for the DiscountLevel object.
     * @return The updated DiscountLevel object.
     */
    @Operation(
            summary = "DiscountLevel - Update discount level",
            description = "Update existing DiscountLevel object.",
            method = "PUT",
            parameters = @Parameter(name = "id", description = "ID for the object to update.", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "DiscountLevel"),
            tags = "DiscountLevelController"
    )
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DiscountLevel updateDiscountLevel(@PathVariable("id") Long id, @RequestBody DiscountLevel discountLevel) {
        return service.updateDiscountLevel(id, discountLevel);
    }
}
