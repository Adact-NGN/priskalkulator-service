package no.ding.pk.repository.specifications;

import no.ding.pk.domain.bo.ConditionCode;
import org.springframework.data.jpa.domain.Specification;

public class TitleTypeSpecification {

    public static Specification<ConditionCode> withTitleType(String titleType) {
        if(titleType == null) {
            return null;
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("code"), titleType);
        }
    }
}
