package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.SalesOrgDTO;
import no.ding.pk.web.dto.v1.web.client.ZoneDTO;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;


public interface SalesOrgService {
    List<SalesOrgDTO> getAll();

    List<SalesOrgDTO> findByQuery(String string, Integer skipTokens);

    List<SalesOrgDTO> getAllBySalesOrganization(String salesOrg);

    List<SalesOrgDTO> getAllBySalesOffice(String salesOffice);

    List<SalesOrgDTO> getAllByPostalNumber(String postalCode);

    List<SalesOrgDTO> getAllBySalesZone(String salesZone);

    List<SalesOrgDTO> getAllByCity(String city);

    @Cacheable(cacheNames = "sapZoneCache", key = "#salesOffice")
    List<ZoneDTO> getZonesForSalesOffice(String salesOffice, String postalCode);
}
