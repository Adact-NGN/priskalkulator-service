package no.ding.pk.service;

import java.util.List;

import no.ding.pk.web.dto.SalesOrgDTO;

public interface SalesOrgService {
    List<SalesOrgDTO> getAll();

    List<SalesOrgDTO> findByQuery(String string, Integer skipTokens);

    List<SalesOrgDTO> getAllBySalesOrganization(String salesOrg);

    List<SalesOrgDTO> getAllBySalesOffice(String salesOffice);

    List<SalesOrgDTO> getAllByPostalNumber(String postalNumber);

    List<SalesOrgDTO> getAllBySalesZone(String salesZone);

    List<SalesOrgDTO> getAllByCity(String city);
}
