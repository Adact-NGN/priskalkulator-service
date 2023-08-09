package no.ding.pk.service.sap;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;

import java.util.List;

public interface StandardPriceService {
    List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg, String zone);

    List<MaterialPrice> getStandardPriceForSalesOrgAndSalesOffice(String salesOrg, String salesOffice, String zone);

    List<MaterialStdPriceDTO> getStandardPriceForSalesOrgSalesOfficeAndMaterial(String salesOrg, String salesOffice, String material, String zone);

    List<MaterialStdPriceDTO> getStandardPriceForMaterialInList(String salesOrg, String salesOffice, List<String> materialNumbers);

    List<MaterialStdPriceDTO> getStandardPriceDTO(String salesOrg, String salesOffice, String material);
}
