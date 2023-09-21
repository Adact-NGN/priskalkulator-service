package no.ding.pk.repository;

import no.ding.pk.domain.PowerOfAttorney;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOfficePowerOfAttorneyRepository extends JpaRepository<PowerOfAttorney, Long> {
    List<PowerOfAttorney> findAllBySalesOfficeIsIn(List<Integer> salesOffices);
    PowerOfAttorney findBySalesOffice(Integer salesOffice);
}
