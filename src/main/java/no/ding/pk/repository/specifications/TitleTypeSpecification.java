package no.ding.pk.repository.specifications;

import no.ding.pk.domain.bo.TitleType;
import org.springframework.data.jpa.domain.Specification;

public class TitleTypeSpecification {

    public static Specification<TitleType> withTitleType(String titleType) {
        if(titleType == null) {
            return null;
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("titleType"), titleType);
        }
    }
}
