package no.ding.pk.repository.specifications;

import no.ding.pk.domain.offer.CustomerTerms;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class CustomerTermsSpecifications {
    public static Specification<CustomerTerms> withAgreementEndDateGreaterThan(Date endDate) {
        if(endDate == null) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("agreementEndDate")));
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("agreementEndDate"), endDate));
    }

    public static Specification<CustomerTerms> withCustomerNumber(String customerNumber) {
        if(customerNumber == null) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("customerNumber"), customerNumber));
    }

    public static Specification<CustomerTerms> withSalesOffice(String salesOffice) {
        if(salesOffice == null) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("salesOffice"), salesOffice));
    }
}
