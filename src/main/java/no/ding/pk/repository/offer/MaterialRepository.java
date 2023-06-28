package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material findByMaterialNumber(String materialNumber);
    Material findByMaterialNumberAndDeviceType(String materialNumber, String deviceType);
}
