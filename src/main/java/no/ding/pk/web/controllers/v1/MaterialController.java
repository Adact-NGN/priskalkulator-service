package no.ding.pk.web.controllers.v1;

import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/material")
public class MaterialController {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    private final SapMaterialService service;

    @Autowired
    public MaterialController(@Qualifier("sapMaterialServiceImpl") SapMaterialService service) {
        this.service = service;
    }

    /**
     * Get Material by sales orfanization and material number
     * @param salesOrg The sales organization number
     * @param material The material number
     * @return {@code MaterialDTO} if material is found, else empty object.
     */
    @GetMapping(path = "/{salesOrg}/{material}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('SCOPE_Sales')")
    public MaterialDTO getMaterialByMaterialNumber(@PathVariable(value = "salesOrg") String salesOrg,
                                                         @PathVariable(value = "material") String material) {
        log.debug("Getting material {} for sales organization {}", material, salesOrg);
        return service.getMaterialByMaterialNumberAndSalesOrg(material, salesOrg);
    }

    /**
     * Get Material by material number, sales organization and sales office
     * @param salesOrg The sales organization number
     * @param salesOffice The sales office number
     * @param material The material number
     * @return {@code MaterialDTO} if material is found, else empty object.
     */
    @GetMapping(path = "/{salesOrg}/{salesOffice}/{material}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MaterialDTO getMaterialByMaterialNumberAndSalesOffice(@PathVariable(value = "salesOrg") String salesOrg,
                                                                       @PathVariable(value = "salesOffice") String salesOffice,
                                                                       @PathVariable(value = "material") String material) {
        log.debug("Getting material {} for sales organization {} and sales office {}", material, salesOrg, salesOffice);

        return service.getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(material, salesOrg, salesOffice, null);
    }

    /**
     * Get all materials for given sales organization number
     * @param salesOrg The sales organization number
     * @param page Selected page to view, default 0 (first page)
     * @param pageSize How many entries a page can include, default 5000
     * @return List of {@code MaterialDTO}, else empty
     */
    @GetMapping(path = "/list/{salesOrg}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialDTO> getMaterialList(@PathVariable(value = "salesOrg") String salesOrg,
                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                             @RequestParam(value = "pageSize", defaultValue = "5000") Integer pageSize) {
        log.debug("Getting materials for sales organization: {}", salesOrg);
        return service.getAllMaterialsForSalesOrg(salesOrg, page, pageSize);
    }

    /**
     * Get a list of materials with standard price included.
     * @param salesOrg sales organization number. Required!
     * @param salesOffice sales office number. Required!
     * @param zone given zone to get materials for. Not required! Set a numeric value with a zero as a suffix to get zone prices. Format ex. 02
     * @param page selected page to view. Default 0
     * @param pageSize page size to view. Default 5000
     * @return list of Materials with standard price
     */
    @GetMapping(path = "/list/{salesOrg}/{salesOffice}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialDTO> getMaterialsWithStdPrice(@PathVariable(value = "salesOrg") String salesOrg,
                                                      @PathVariable(value = "salesOffice") String salesOffice,
                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "pageSize", defaultValue = "5000") Integer pageSize,
                                                      @RequestParam(value = "zone", required = false) String zone) {
        log.debug("Getting materials for salesOrg: {} and salesOffice: {} for zone: {}", salesOrg, salesOffice, zone != null ? zone : "no zone");

        return service.getAllMaterialsForSalesOrgAndSalesOffice(salesOrg, salesOffice, zone, page, pageSize);
    }
}
