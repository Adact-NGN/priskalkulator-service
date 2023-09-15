package no.ding.pk.repository.specifications;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import java.util.List;

public class DiscountSpecifications {

    public static Specification<Discount> withSalesOrg(String salesOrg) {
        if(salesOrg == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesOrg"), salesOrg);
        }
    }

    public static Specification<Discount> withSalesOffice(String salesOffice) {
        if(salesOffice == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesOffice"), salesOffice);
        }
    }

    public static Specification<Discount> withMaterialNumber(String materialNumber) {
        if(materialNumber == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("materialNumber"), materialNumber);
        }
    }

    public static Specification<Discount> hasDiscountLevelInZone(Integer zone) {
        if(zone == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Join<DiscountLevel,Discount> discountLevels = root.join("discountLevels");
            return criteriaBuilder.equal(discountLevels.get("zone"), zone);
        };
    }

    public static Specification<Discount> hasDiscountLevelZoneInList(List<Integer> zones) {
        return (root, query, criteriaBuilder) -> {
            Join<DiscountLevel, Discount> discountLevels = root.join("discountLevels");
            return discountLevels.in(zones);
        };
    }

    public static Specification<Discount> matchMaterialNumberInList(List<String> materialNumbers) {
        return (root, query, criteriaBuilder) -> {
            Path<Discount> materialNumber = root.get("materialNumber");
            return materialNumber.in(materialNumbers);
        };
    }
}
