package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.offer.PriceOfferTemplate;
import no.ding.pk.service.offer.PriceOfferTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/price-offer-template")
public class PriceOfficeTemplateController {
    
    private PriceOfferTemplateService service;

    @Autowired
    public PriceOfficeTemplateController(PriceOfferTemplateService service) {
        this.service = service;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplate createTemplate(@RequestBody PriceOfferTemplate newTemplate) {
        return service.save(newTemplate);
    }
}
