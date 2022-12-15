package no.ding.pk.repository.offer;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ding.pk.domain.offer.Terms;

public interface CustomerTermsRepository extends JpaRepository<Terms, Long> {
    
}
