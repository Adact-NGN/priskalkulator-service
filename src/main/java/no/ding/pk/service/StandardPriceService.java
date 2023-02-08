package no.ding.pk.service;

import java.util.List;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;

public interface StandardPriceService {
    List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg);

    MaterialPrice getStandardPriceForMaterial(String materialNumber, String salesOrg, String salesOffice);
}
