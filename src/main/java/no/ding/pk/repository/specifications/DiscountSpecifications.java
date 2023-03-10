package no.ding.pk.repository.specifications;

import no.ding.pk.domain.Discount;
import org.springframework.data.jpa.domain.Specification;

public class DiscountSpecifications {

    public static Specification<Discount> withSalesOrg(String salesOrg) {
        if(salesOrg == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesOrg"), salesOrg);
        }
    }

    public static Specification<Discount> withZone(String zone) {
        if(zone == null) {
            return (root, query, cb) -> cb.conjunction();
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
}
