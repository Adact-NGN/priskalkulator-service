package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.MaterialDTO;

import java.util.List;

public interface SapMaterialService {
    MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String salesOrg, String material);
    MaterialDTO getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(String material, String salesOrg,
            String salesOffice, String zone);
    List<MaterialDTO> getAllMaterialsForSalesOrgBy(String salesOrg, Integer page, Integer pageSize);
}
