package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.CustomerTerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CustomerTermsRepository extends JpaRepository<CustomerTerms, Long> {
    @Query("select ct from CustomerTerms as ct where ct.salesOffice = :salesOffice and ct.customerNumber = :customerNumber")
    List<CustomerTerms> findAllBySalesOfficeAndCustomerNumber(@Param("salesOffice") String salesOffice, @Param("customerNumber") String customerNumber);
    Integer countAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber);

    List<CustomerTerms> findBySalesOrgAndSalesOfficeAndCustomerNumberAndAgreementEndDateGreaterThanOrAgreementEndDateIsNullOrderByCreatedDateDesc(String salgsOrg, String salesOffice, String customerNumber, Date date);
}
