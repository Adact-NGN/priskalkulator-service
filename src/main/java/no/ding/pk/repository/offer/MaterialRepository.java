package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, JpaSpecificationExecutor<Material> {
    Optional<Material> findByMaterialNumber(String materialNumber);
    Optional<Material> findByMaterialNumberAndDeviceType(String materialNumber, String deviceType);

    Optional<Material> findByMaterialNumberAndDeviceTypeAndSalesZone(String materialNumber, String deviceType, String salesZone);
}
