package no.ding.pk.web.controllers;

import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.service.DiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discount/level")
public class DiscountLevelController {

    private final static Logger log = LoggerFactory.getLogger(DiscountLevelController.class);

    private final DiscountService service;

    @Autowired
    public DiscountLevelController(@Qualifier("discountServiceImpl") DiscountService service) {
        this.service = service;
    }

    /**
     * Get discount level for a specific material which  belongs to a given material. The material resides under a sales organization and sales office.
     * @param salesOrg Sales organization number
     * @param materialNumber Material number
     * @param level Discount level
     * @return A list with all the discount levels returned for the criteria given, else empty list.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscountLevel> getSpecificDiscountLevel(@RequestParam("salesOrg") String salesOrg,
    @RequestParam("materialNumber") String materialNumber,
    @RequestParam(name = "level", required = false) Integer level) {
        log.debug(String.format("Getting discount level for: salesOrg: %s materialNumber: %s level: %d", salesOrg, materialNumber, level));
        return service.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, materialNumber, level);
    }

    /**
     * Get a list of discount levels for a specific material or a list of material numbers.
     *
     * @param salesOrg       Sales organization number
     * @param materialNumber Material number or a comma separated number list.
     * @param zone           Specify for which zone to get discount levels for.
     * @return A list of all the discount levels for one or multiple materials, else empty list.
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscountLevel> getAllDiscountLevelsForSpecificDiscount(@RequestParam("salesOrg") String salesOrg,
    @RequestParam("materialNumber") String materialNumber, @RequestParam(value = "zone", required = false) String zone) {
        log.debug(String.format("Getting all discount levels for: salesOrg: %s materialNumber: %s", salesOrg, materialNumber));
        return service.findAllDiscountLevelsForDiscountBySalesOrgAndMaterialNumber(salesOrg, materialNumber, zone);
    }

    /**
     * Update existing DiscountLevel object.
     * @param id The object id to look up the object with.
     * @param discountLevel New values for the DiscountLevel object.
     * @return The updated DiscountLevel object.
     */
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DiscountLevel updateDiscountLevel(@PathVariable("id") Long id, @RequestBody DiscountLevel discountLevel) {
        return service.updateDiscountLevel(id, discountLevel);
    }
}
