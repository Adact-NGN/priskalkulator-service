package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.PriceOfferTerms;
import no.ding.pk.repository.offer.PriceOfferTermsRepository;
import org.springframework.stereotype.Service;

@Service
public class PriceOfferTermsServiceImpl implements  PriceOfferTermsService{

    private final PriceOfferTermsRepository repository;

    public PriceOfferTermsServiceImpl(PriceOfferTermsRepository repository) {
        this.repository = repository;
    }

    @Override
    public PriceOfferTerms save(PriceOfferTerms priceOfferTerms) {
        return repository.save(priceOfferTerms);
    }
}
