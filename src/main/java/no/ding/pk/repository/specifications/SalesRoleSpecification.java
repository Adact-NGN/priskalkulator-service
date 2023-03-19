package no.ding.pk.repository.specifications;

import jakarta.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;

public class SalesRoleSpecification {
    
    public static Specification<SalesRole> hasUserWithId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            Join<User, SalesRole> userSalesRole = root.join("userList");
            return criteriaBuilder.equal(userSalesRole.get("id"), userId);
        };
    }
}
