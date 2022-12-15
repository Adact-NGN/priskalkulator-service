package no.ding.pk.service.offer;

import java.util.Optional;

import no.ding.pk.domain.offer.MaterialPrice;

public interface MaterialPriceService {

    MaterialPrice save(MaterialPrice materialPrice);
    Optional<MaterialPrice> findById(Long id);
    MaterialPrice findByMaterialNumber(String materialNumber);
}
