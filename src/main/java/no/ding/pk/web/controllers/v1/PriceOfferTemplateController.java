package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.offer.PriceOfferTemplate;
import no.ding.pk.service.offer.PriceOfferTemplateService;
import no.ding.pk.web.handlers.PriceOfferTemplateNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/price-offer-template")
public class PriceOfferTemplateController {
    
    private final PriceOfferTemplateService service;

    @Autowired
    public PriceOfferTemplateController(PriceOfferTemplateService service) {
        this.service = service;
    }

    /**
     * List all Price offer templates.
     * @return A list of PriceOfferTemplates
     */
    @GetMapping(path = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferTemplate> getAllTemplates() {
        return service.findAll();
    }

    /**
     * Get a specific PriceOfferTemplate
     * @param id the id for the template
     * @return PriceOfferTemplate
     */
    @GetMapping(path = "/{id}")
    public PriceOfferTemplate getTemplateById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    /**
     * Create new PriceOfferTemplate
     * @param newTemplate the object
     * @return Newly created PriceOfferTemplate object.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplate createTemplate(@RequestBody PriceOfferTemplate newTemplate) {
        return service.save(newTemplate);
    }

    /**
     * Update existing PriceOfferTemplate
     * @param priceOfferTemplate object with updated values.
     * @return Updated PriceOfferTemplate object.
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplate save(@RequestBody PriceOfferTemplate priceOfferTemplate) {
        return service.save(priceOfferTemplate);
    }

    @ExceptionHandler({PriceOfferTemplateNotFound.class})
    public ResponseEntity<Object> handleNotFoundException() {
        return new ResponseEntity<>("Price Offer Template was not found.", HttpStatus.NOT_FOUND);
    }
}
