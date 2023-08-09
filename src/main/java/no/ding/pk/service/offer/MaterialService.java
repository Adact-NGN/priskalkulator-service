package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.offer.Material;

public interface MaterialService {

    Material save(Material material);
    List<Material> saveAll(List<Material> materialList);
    Optional<Material> findById(Long id);
    Material findByMaterialNumber(String materialNumber);

    Material findByMaterialNumberAndDeviceType(String material, String deviceType);
}
