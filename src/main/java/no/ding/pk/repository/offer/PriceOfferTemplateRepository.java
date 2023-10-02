package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceOfferTemplateRepository extends JpaRepository<PriceOfferTemplate, Long> {
    
}
