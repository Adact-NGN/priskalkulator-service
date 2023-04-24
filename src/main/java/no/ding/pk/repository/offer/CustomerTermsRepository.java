package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.CustomerTerms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerTermsRepository extends JpaRepository<CustomerTerms, Long> {
    List<CustomerTerms> findAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber);
    Integer countAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber);
}
