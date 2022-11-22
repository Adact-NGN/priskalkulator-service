package no.ding.pk.web.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.service.DiscountService;

@RestController
@RequestMapping("/api/discount/level")
public class DiscountLevelController {

    private final static Logger log = LoggerFactory.getLogger(DiscountLevelController.class);

    private DiscountService service;

    @Autowired
    public DiscountLevelController(DiscountService service) {
        this.service = service;
    }

    /**
     * Get discount level for a specific material which  belongs to a given material. The material resides under a sales organization and sales office.
     * @param salesOrg Sales organization number
     * @param salesOffice Sales office number
     * @param materialNumber Material number
     * @param level Discount level
     * @return A list with all the discount levls returned for the criterias given, else empty list.
     */
    @GetMapping()
    public List<DiscountLevel> getSpecificDiscountLevel(@RequestParam("salesOrg") String salesOrg,
    @RequestParam("materialNumber") String materialNumber,
    @RequestParam("level") int level) {
        log.debug(String.format("Getting discount level for: salesOrg: %s materialNumber: %s level: %d", salesOrg, materialNumber, level));
        return service.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, materialNumber, level);
    }

    /**
     * Get a list of discount levels for a specific material or a list of material numbers.
     * @param salesOrg Sales organization number
     * @param materialNumber Material number or a comma separated number list.
     * @return A list of all the discount levels for one or multiple materials, else empty list.
     */
    @GetMapping("/list")
    public List<DiscountLevel> getAllDiscountLevelsForSpecificDiscount(@RequestParam("salesOrg") String salesOrg,
    @RequestParam("materialNumber") String materialNumber) {
        log.debug(String.format("Getting all discount levels for: salesOrg: %s materialNumber: %s", salesOrg, materialNumber));
        return service.findAllDiscountLevelsForDiscountBySalesOrgAndMaterialNumber(salesOrg, materialNumber);
    }

    /**
     * Update existing DiscountLevel object.
     * @param id The object id to look up the object with.
     * @param discountLevel New values for the DiscountLevel object.
     * @return The updated DiscountLevel object.
     */
    @PutMapping("/{id}")
    public DiscountLevel updateDiscountLevel(@PathVariable("id") Long id, @RequestBody DiscountLevel discountLevel) {
        return service.updateDiscountLevel(id, discountLevel);
    }
}
