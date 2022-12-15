package no.ding.pk.service.offer;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.PriceOfferTemplate;
import no.ding.pk.repository.offer.PriceOfferTemplateRepository;

@Transactional
@Service
public class PriceOfferTemplateServiceImpl implements PriceOfferTemplateService {

    private PriceOfferTemplateRepository repository;
    
    @Autowired
    public PriceOfferTemplateServiceImpl(PriceOfferTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public PriceOfferTemplate save(PriceOfferTemplate newTemplate) {
        return repository.save(newTemplate);
    }
}
