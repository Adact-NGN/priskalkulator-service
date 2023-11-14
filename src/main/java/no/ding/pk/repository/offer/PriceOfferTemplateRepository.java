package no.ding.pk.repository.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceOfferTemplateRepository extends JpaRepository<PriceOfferTemplate, Long> {

    List<PriceOfferTemplate> findAllByAuthorEmail(String userEmail);

    List<PriceOfferTemplate> findAllBySharedWith(User user);
}
