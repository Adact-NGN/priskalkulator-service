package no.ding.pk.repository.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.offer.SalesOffice;

@Repository
public interface SalesOfficeRepository extends JpaRepository<SalesOffice, Long> {
    SalesOffice findByPostalNumberAndSalesOfficeAndSalesOrg(String postalCode, String salesOffice, String salesOrg);
}
