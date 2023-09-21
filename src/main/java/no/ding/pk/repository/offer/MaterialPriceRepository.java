package no.ding.pk.repository.offer;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ding.pk.domain.offer.MaterialPrice;

public interface MaterialPriceRepository extends JpaRepository<MaterialPrice, Long> {
    MaterialPrice findByMaterialNumber(String materialNumber);
}
