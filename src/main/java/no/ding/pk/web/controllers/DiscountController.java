package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.Discount;
import no.ding.pk.service.DiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "DiscountController", description = "Get discounts for sales organization")
@RestController
@RequestMapping(value = {"/api/discount", "/api/v1/discount"})
public class DiscountController {

    private static final Logger log = LoggerFactory.getLogger(DiscountController.class);

    private final DiscountService service;

    @Autowired
    public DiscountController(DiscountService service) {
        this.service = service;
    }

    /**
     * A list of all the Discount objects.
     * @return List of all Discount objects.
     */
    @Operation(
            summary = "Discount - Get list of discounts",
            description = "Get list of all Discount objects",
            method = "GET",
            tags = "DiscountController"
    )
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Discount> getAllDiscounts() {
        return service.findAll();
    }

    /**
     * Get a list of all discounts for a given salesorg, material number and zone
     * @param salesOrg Sales organization number
     * @param salesOffice Sales office number
     * @param materialNumber Material number to look up, not required
     * @param zone Which zone to get discount for, not required
     * @return A list of all discounts, else empty list
     */
    @Operation(
            summary = "Discount - Get list of discounts by sales org, material number and zone",
            description = "Get a list of all discount for a given sales organization, material number and zone",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales organization number.", required = true),
                    @Parameter(name = "salesOffice", description = "Sales office number", required = true),
                    @Parameter(name = "materialNumber", description = "Material number to look up."),
                    @Parameter(name = "zone", description = "Which zone to get discounts for.")
            },
            tags = "DiscountController"
    )
    @GetMapping(path = "/list/{salesOrg}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Discount> getAllDiscountsForSalesOrg(@PathVariable("salesOrg") String salesOrg,
                                                     @RequestParam(value = "salesOffice") String salesOffice,
                                                     @RequestParam(value = "materialNumber", required = false) String materialNumber,
                                                     @RequestParam(value = "zone", required = false) String zone) {
        return service.findAllBySalesOrgAndSalesOfficeAndZoneAndMaterialNumber(salesOrg, salesOffice, zone, materialNumber);
    }

    /**
     * Returns a list over all discounts for a given sales organization and for a list of material numbers.
     * @param salesOrg Sales organization number
     * @param salesOffice Sales office number
     * @param materialNumbers Comma separated list of material numbers.
     * @param zones Which zones to get discount for, not required.
     * @return A list of all discounts, else empty list
     */
    @Operation(
            summary = "Discount - Get discounts for sales org, sales office and material numbers",
            description = "Returns a list over all discounts for a given sales organization and for a list of material numbers.",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales organization number.", required = true),
                    @Parameter(name = "salesOffice", description = "Sales office number", required = true),
                    @Parameter(name = "materialNumbers", description = "Comma separated list of material numbers.", required = true),
                    @Parameter(name = "zone", description = "Which zone to get discounts for.")
            },
            tags = "DiscountController"
    )
    @GetMapping(path = "/in-list/{salesOrg}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Discount> getAllDiscountsForSalesOrgAndMaterialNumbersInList(@PathVariable("salesOrg") String salesOrg,
                                                                             @RequestParam(value = "salesOffice") String salesOffice,
                                                                             @RequestParam(value = "materialNumbers") String materialNumbers,
                                                                             @RequestParam(value = "zones", required = false) String zones) {
        log.debug("Getting discount for material {} and zone {} in sales org {}", materialNumbers, zones, salesOrg);
        return service.findAllBySalesOrgAndSalesOfficeAndMaterialNumber(salesOrg, salesOffice, materialNumbers, zones);
    }

    /**
     * Create a new Discount object and persist it to the database.
     * @param discount - The Discount object.
     * @return The persisted Discount object with its own id.
     */
    @Operation(
            summary = "Discount - Create discount",
            description = "Create a new Discount object and persist it.",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "Discount", required = true),
            tags = "DiscountController"
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Discount createDiscount(@RequestBody Discount discount) {
        log.debug("Creating Discount object.");
        return service.save(discount);
    }

    /**
     * A batch job to create multiple new Discount objects.
     * @param discounts A list of Discount objects to be created.
     * @return A list of all the newly created Discount objects.
     */
    @Operation(
            summary = "Discount - Create multiple discounts",
            description = "A batch job to create multiple new Discount object and persist them.",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "Discount", required = true),
            tags = "DiscountController"
    )
    @PostMapping(path = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Discount> createDiscounts(@RequestBody List<Discount> discounts) {
        log.debug("Batch creating Discount objects. Amount of items: " + discounts.size());
        return service.saveAll(discounts);
    }

    /**
     * Update an existing Discount object.
     * @param id The object id to look up the object with.
     * @param discount New values for the existing Discount object.
     * @return The updated Discount object.
     */
    @Operation(
            summary = "Discount - Update discount",
            description = "Update an existing Discount object",
            method = "PUT",
            parameters = @Parameter(name = "id", description = "ID for discount to update", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "Discount", required = true),
            tags = "DiscountController"
    )
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Discount updateDiscount(@PathVariable("id") Long id, @RequestBody Discount discount) {
        log.debug("Updating Discount object with id: " + id);
        return service.update(id, discount);
    }
}
