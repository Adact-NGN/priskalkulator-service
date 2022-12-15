package no.ding.pk.service.offer;

import java.util.Optional;

import no.ding.pk.domain.offer.Terms;

public interface CustomerTermsService {

    Optional<Terms> findById(Long id);
    Terms save(Terms customerTerms);

}
