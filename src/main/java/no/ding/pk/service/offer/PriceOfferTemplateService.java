package no.ding.pk.service.offer;


import no.ding.pk.domain.offer.template.PriceOfferTemplate;

import java.util.List;

public interface PriceOfferTemplateService {

    PriceOfferTemplate save(PriceOfferTemplate newTemplate);

    List<PriceOfferTemplate> findAll();

    PriceOfferTemplate findById(Long id);
}
