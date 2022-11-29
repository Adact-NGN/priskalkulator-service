package no.ding.pk.web.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.domain.Discount;
import no.ding.pk.service.DiscountService;

@RestController
@RequestMapping("/api/discount")
public class DiscountController {
    
    private static final Logger log = LoggerFactory.getLogger(DiscountController.class);
    
    private DiscountService service;
    
    @Autowired
    public DiscountController(DiscountService service) {
        this.service = service;
    }
    
    /**
     * A list of all the Discount objects.
     * @return List of all Discount objects.
     */
    @GetMapping("/list")
    public List<Discount> getAllDiscounts() {
        return service.findAll();
    }
    
    /**
    * Get a list of all discounts for a given salesorg and sales office
    * @param salesOrg Sales organization number
    * @param salesOffice Sales office number
    * @return A list of all discounts, else empty list
    */
    @GetMapping("/list/{salesOrg}")
    public List<Discount> getAllDiscountsForSalesOrg(@PathVariable("salesOrg") String salesOrg,
    @RequestParam(value = "materialNumber", required = false) String materialNumber, @RequestParam(value = "zone", required = false) String zone) {
        return service.findAllBySalesOrgAndZoneAndMaterialNumber(salesOrg, zone, materialNumber);
    }

    @GetMapping("/in-list/{salesOrg}")
    public List<Discount> getAllDiscountsForSalesOrgAndMaterialNumbersInList(@PathVariable("salesOrg") String salesOrg, @RequestParam(value = "materialNumbers") String materialNumbers) {
        return service.findAllBySalesOrgAndMaterialNumber(salesOrg, materialNumbers);
    }
    
    /**
     * Create a new Discount object and persist it to the database.
     * @param discount - The Discount object.
     * @return The persisted Discount object with its own id.
     */
    @PostMapping()
    public Discount createDiscount(@RequestBody Discount discount) {
        log.debug("Creating Discount object.");
        return service.save(discount);
    }
    
    /**
    * A batch job to create multiple new Discount objects.
    * @param discounts A list of Discount objects to be created.
    * @return A list of all the newly created Discount objects.
    */
    @PostMapping("/batch")
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
    @PutMapping("/{id}")
    public Discount updateDiscount(@PathVariable("id") Long id, @RequestBody Discount discount) {
        log.debug("Updating Disvount object with id: " + id);
        return service.update(id, discount);
    }
}
