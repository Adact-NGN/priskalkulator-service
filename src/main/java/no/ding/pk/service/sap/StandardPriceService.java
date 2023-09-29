package no.ding.pk.service.sap;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;

import java.util.List;
import java.util.Map;

public interface StandardPriceService {
    List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg, String zone);

    Map<String, MaterialPrice> getStandardPriceForSalesOrgAndSalesOfficeMap(String salesOrg, String salesOffice, String zone);

    List<MaterialStdPriceDTO> getStandardPriceForSalesOrgSalesOfficeAndMaterial(String salesOrg, String salesOffice, String material, String zone);

    List<MaterialStdPriceDTO> getStandardPriceDTO(String salesOrg, String salesOffice, String material);
}
