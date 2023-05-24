package no.ding.pk.service;

import no.ding.pk.domain.PowerOfAttorney;

import java.util.List;

public interface SalesOfficePowerOfAttorneyService {
    List<PowerOfAttorney> findAll();

    PowerOfAttorney save(PowerOfAttorney poa);

    PowerOfAttorney findById(Long id);

    List<PowerOfAttorney> findBySalesOfficeInList(List<Integer> salesOffices);

    boolean delete(Long id);

    PowerOfAttorney findBySalesOffice(Integer salesOfficeNumber);
}
