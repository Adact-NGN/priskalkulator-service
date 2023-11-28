package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.MaterialDTO;

import java.util.List;

public interface SapMaterialService {
    MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String material, String salesOrg);
    MaterialDTO getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(String salesOrg, String salesOffice, String material,
            String zone);
    List<MaterialDTO> getAllMaterialsForSalesOrgByZone(String salesOrg, Integer page, Integer pageSize);
    List<MaterialDTO> getAllMaterialsForSalesOrgByZone(String salesOrg, String zone, Integer page, Integer pageSize);
}
