package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.offer.MaterialPrice;

public interface MaterialPriceService {

    MaterialPrice save(MaterialPrice materialPrice);
    Optional<MaterialPrice> findById(Long id);
    MaterialPrice findByMaterialNumber(String materialNumber);
    List<MaterialPrice> findAll();

    Optional<MaterialPrice> findByMaterialNumberDeviceTypeAndSalesZone(String materialNumber, String deviceType, String salesZone);
}
