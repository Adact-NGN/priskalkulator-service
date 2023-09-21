package no.ding.pk.repository.specifications;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

public class ApprovalSpecifications {
    public static Specification<PriceOffer> withPriceOfferStatus(String priceOfferStatus) {
        if(priceOfferStatus == null) {
            return null;
        } else
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priceOfferStatus"), priceOfferStatus);
    }

    public static Specification<PriceOffer> withIsApproved(Boolean isApproved) {
        if(isApproved == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("isApproved"));
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isApproved"), isApproved);
        }
    }

    public static Specification<PriceOffer> withNeedsApproval(Boolean needsApproval) {
        if(needsApproval == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("needsApproval"));
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("needsApproval"), needsApproval);
        }
    }

    public static Specification<PriceOffer> withApproverId(Long id) {
        if(id == null) {
            return null;
        } else {
            return (root, query, criteriaBuilder) -> {
                Join<PriceOffer, User> priceOfferApprover = root.join("approver");
                return criteriaBuilder.equal(priceOfferApprover.get("id"), id);
            };
        }
    }
}
