package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.offer.Material;

public interface MaterialService {

    List<Material> findAll();

    Material save(Material material);
    List<Material> saveAll(List<Material> materialList);
    Optional<Material> findById(Long id);
    Optional<Material> findByMaterialNumber(String materialNumber);

    Optional<Material> findByMaterialNumberAndDeviceType(String material, String deviceType);

    Optional<Material> findBy(String salesOrg, String salesOffice, String materialNumber, String deviceType, String zone);
}
