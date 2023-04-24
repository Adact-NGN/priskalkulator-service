package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.MaterialDTO;

import java.util.List;

public interface SapMaterialService {
    MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String material, String salesOrg);
    MaterialDTO getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(String material, String salesOrg,
            String salesOffice, String zone);
    List<MaterialDTO> getAllMaterialsForSalesOrg(String salesOrg, Integer page, Integer pageSize);
    List<MaterialDTO> getAllMaterialsForSalesOrgAndSalesOffice(String salesOrg, String salesOffice, String zone, Integer page, Integer pageSize);
}
