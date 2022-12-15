package no.ding.pk.repository.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.offer.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Material findByMaterialNumber(String materialNumber);
    
}
