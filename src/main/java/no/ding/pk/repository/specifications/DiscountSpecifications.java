package no.ding.pk.repository.specifications;

import no.ding.pk.domain.Discount;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import java.util.List;

public class DiscountSpecifications {

    public static Specification<Discount> withSalesOrg(String salesOrg) {
        if(salesOrg == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesOrg"), salesOrg);
        }
    }

    public static Specification<Discount> withZone(String zone) {
        if(zone == null || zone.equals("")) {
            return (root, query, cb) -> cb.isNull(root.get("zone"));
        } else {
            return (root, query, cb) -> cb.equal(root.get("zone"), zone);
        }
    }

    public static Specification<Discount> withMaterialNumber(String materialNumber) {
        if(materialNumber == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("materialNumber"), materialNumber);
        }
    }

    public static Specification<Discount> matchMaterialNumberInList(List<String> materialNumbers) {
        return (root, query, criteriaBuilder) -> {
            Path<Discount> materialNumber = root.get("materialNumber");
            return materialNumber.in(materialNumbers);
        };
    }
}
