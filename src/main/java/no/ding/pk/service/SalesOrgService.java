package no.ding.pk.service;

import no.ding.pk.web.dto.sap.SalesOrgDTO;

import java.util.List;


public interface SalesOrgService {
    List<SalesOrgDTO> getAll();

    List<SalesOrgDTO> findByQuery(String string, Integer skipTokens);

    List<SalesOrgDTO> getAllBySalesOrganization(String salesOrg);

    List<SalesOrgDTO> getAllBySalesOffice(String salesOffice);

    List<SalesOrgDTO> getAllByPostalNumber(String postalCode);

    List<SalesOrgDTO> getAllBySalesZone(String salesZone);

    List<SalesOrgDTO> getAllByCity(String city);
}
