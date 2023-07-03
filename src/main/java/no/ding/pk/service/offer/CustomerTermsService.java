package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.CustomerTerms;

import java.util.List;
import java.util.Optional;

public interface CustomerTermsService {

    Optional<CustomerTerms> findById(Long id);
    CustomerTerms update(CustomerTerms customerTerms);

    List<CustomerTerms> findAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber);

    Integer countAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber);

    CustomerTerms save(String salesOffice, String customerNumber, CustomerTerms customerTerms);

    List<CustomerTerms> findAll(String salesOffice, String customerNumber);
    CustomerTerms findActiveTermsForCustomerForSalesOfficeAndSalesOrg(String customerNumber, String salesOffice,
            String salesOrg);

    List<CustomerTerms> findAllActive(String salesOffice, String customerNumber);
}
