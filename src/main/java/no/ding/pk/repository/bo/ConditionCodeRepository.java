package no.ding.pk.repository.bo;

import no.ding.pk.domain.bo.ConditionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionCodeRepository extends JpaRepository<ConditionCode, Long>, JpaSpecificationExecutor<ConditionCode> {
    ConditionCode findConditionCodeByCode(String conditionCode);
}
