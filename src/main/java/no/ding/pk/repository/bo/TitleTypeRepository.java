package no.ding.pk.repository.bo;

import no.ding.pk.domain.bo.TitleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleTypeRepository extends JpaRepository<TitleType, Long>, JpaSpecificationExecutor<TitleType> {
}
