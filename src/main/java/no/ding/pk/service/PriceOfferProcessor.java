package no.ding.pk.service;

import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.repository.offer.PriceOfferRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

@Component
public class PriceOfferProcessor {

    private final PriceOfferRepository priceOfferRepository;
    private final EntityManager entityManager;

    public PriceOfferProcessor(PriceOfferRepository priceOfferRepository, EntityManager entityManager) {
        this.priceOfferRepository = priceOfferRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public void processPriceOffers() {
        Stream<PriceOffer> priceOfferStream = priceOfferRepository.findAllAsStream();

        priceOfferStream.forEach(priceOffer -> entityManager.detach(priceOffer));
    }
}
