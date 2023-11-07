package no.ding.pk.repository.bo;

import no.ding.pk.domain.bo.KeyCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyCombinationRepository extends JpaRepository<KeyCombination, Long>, JpaSpecificationExecutor<KeyCombination> {
}
