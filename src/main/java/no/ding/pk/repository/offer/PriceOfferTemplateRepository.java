package no.ding.pk.repository.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.offer.PriceOfferTemplate;

@Repository
public interface PriceOfferTemplateRepository extends JpaRepository<PriceOfferTemplate, Long> {
    
}
