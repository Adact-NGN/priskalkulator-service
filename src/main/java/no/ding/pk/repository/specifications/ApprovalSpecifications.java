package no.ding.pk.repository.specifications;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.SalesOffice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import java.util.List;

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

    public static Specification<PriceOffer> withSalesEmployeeId(Long id) {
        if(id == null) {
            return null;
        } else {
            return (root, query, criteriaBuilder) -> {
                Join<PriceOffer, User> priceOfferSalesEmployee = root.join("salesEmployee");
                return criteriaBuilder.equal(priceOfferSalesEmployee.get("id"), id);
            };
        }
    }

    public static Specification<PriceOffer> withPriceOfferStatusInList(List<String> statusList) {
        if(statusList == null) {
            return null;
        } else {
            return (root, query, criteriaBuilder) -> {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("priceOfferStatus"));

                for(String status : statusList) {
                    inClause.value(status);
                }

                return inClause;
            };
        }
    }

    public static Specification<PriceOffer> withSalesOfficeInList(List<String> salesOffices) {
        if(salesOffices == null) {
            return null;
        } else {
            return (root, query, criteriaBuilder) -> {
                Join<PriceOffer, SalesOffice> salesOfficeJoin = root.join("salesOffices");

                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(salesOfficeJoin.get("salesOffice"));

                for(String office : salesOffices) {
                    inClause.value(office);
                }

                return inClause;
            };
        }
    }
}
