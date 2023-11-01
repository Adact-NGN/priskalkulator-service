package no.ding.pk.repository.offer;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ding.pk.domain.offer.MaterialPrice;

import java.util.Optional;

public interface MaterialPriceRepository extends JpaRepository<MaterialPrice, Long> {
    MaterialPrice findByMaterialNumber(String materialNumber);
    Optional<MaterialPrice> findMaterialPriceByMaterialNumberAndDeviceTypeAndZone(String materialNumber, String deviceType, String zone);
}
