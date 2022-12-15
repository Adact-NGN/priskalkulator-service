package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.offer.PriceOffer;

public interface PriceOfferService {

    PriceOffer save(PriceOffer newPriceOffer);

    Optional<PriceOffer> findById(Long id);

    List<PriceOffer> findAll();
    
}
