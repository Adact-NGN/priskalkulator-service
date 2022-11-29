package no.ding.pk.repository.specifications;

import org.springframework.data.jpa.domain.Specification;

import no.ding.pk.domain.User;

public class UserSpecification {
    public static Specification<User> hasUserWithId(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), userId);
    }
}
