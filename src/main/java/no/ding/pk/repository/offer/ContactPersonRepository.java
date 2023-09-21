package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.ContactPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactPersonRepository extends JpaRepository<ContactPerson, Long> {
}
