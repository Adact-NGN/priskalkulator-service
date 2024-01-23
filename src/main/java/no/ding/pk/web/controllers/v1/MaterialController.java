package no.ding.pk.web.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Material Controller", description = "Controller for getting material information from SAP.")
@RestController
@RequestMapping("/api/v1/material")
public class MaterialController {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    private final SapMaterialService service;

    @Autowired
    public MaterialController(SapMaterialService service) {
        this.service = service;
    }

    /**
     * Get Material by sales organization and material number
     * @param salesOrg The sales organization number
     * @param material The material number
     * @return {@code MaterialDTO} if material is found, else empty object.
     */
    @Operation(summary = "Get Material by sales organization and material number",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales org number. The sales org to get materials for.", required = true),
                    @Parameter(name = "material", description = "Material number.", required = true),
            },
            tags = "CustomerTermsController"
    )
    @GetMapping(path = "/{salesOrg}/{material}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('SCOPE_Sales')")
    public ResponseEntity<MaterialDTO> getMaterialByMaterialNumber(@PathVariable(value = "salesOrg") String salesOrg,
                                                                  @PathVariable(value = "material") String material) {
        log.debug("Getting material {} for sales organization {}", material, salesOrg);
        MaterialDTO materialDTO = service.getMaterialByMaterialNumberAndSalesOrg(salesOrg, material);

        if(materialDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(materialDTO);
    }


    /**
     * Get Material by material number, sales organization and sales office
     * @param salesOrg The sales organization number
     * @param salesOffice The sales office number
     * @param material The material number
     * @return {@code MaterialDTO} if material is found, else empty object.
     */
    @Operation(deprecated = true,
            description = "Get Material by material number, sales organization and sales office.",
            parameters = {
                    @Parameter(name = "salesOrg", required = true, description = "Sales org to get material for."),
                    @Parameter(name = "salesOffice", required = true, description = "Sales office to get material for."),
                    @Parameter(name = "material", required = true, description = "Material number to get information for."),
            },
            tags = "CustomerTermsController"
    )
    @GetMapping(path = "/{salesOrg}/{salesOffice}/{material}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MaterialDTO getMaterialByMaterialNumberAndSalesOffice(@PathVariable(value = "salesOrg") String salesOrg,
                                                                 @PathVariable(value = "salesOffice") String salesOffice,
                                                                 @PathVariable(value = "material") String material) {
        log.debug("Getting material {} for sales organization {} and sales office {}", material, salesOrg, salesOffice);

        return service.getMaterialByMaterialNumberAndSalesOrg(salesOrg, material);
    }

    /**
     * Get all materials for given sales organization number
     * @param salesOrg The sales organization number
     * @param page Selected page to view, default 0 (first page)
     * @param pageSize How many entries a page can include, default 5000
     * @return List of {@code MaterialDTO}, else empty
     */
    @Operation(
            description = "Get all materials for given sales organization number.",
            parameters = {
                    @Parameter(name = "salesOrg", required = true, description = "Sales org to get material for."),
                    @Parameter(name = "page", required = true, description = "Amount of pages to get", allowEmptyValue = true),
                    @Parameter(name = "pageSize", required = true, description = "Amount for elements per page.", allowEmptyValue = true),
            },
            tags = "CustomerTermsController"
    )
    @GetMapping(path = "/list/{salesOrg}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialDTO> getMaterialList(@PathVariable(value = "salesOrg") String salesOrg,
                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                             @RequestParam(value = "pageSize", defaultValue = "5000") Integer pageSize) {
        log.debug("Getting materials for sales organization: {}", salesOrg);
        return service.getAllMaterialsForSalesOrgBy(salesOrg, page, pageSize);
    }

    /**
     * Get a list of materials with standard price included.
     * @param salesOrg sales organization number. Required!
     * @param salesOffice sales office number.
     * @param zone given zone to get materials for. Not required! Set a numeric value with a zero as a suffix to get zone prices. Format ex. 02
     * @param page selected page to view. Default 0
     * @param pageSize page size to view. Default 5000
     * @deprecated function is similar to {@code getMaterialList(...)}
     * @return list of Materials with standard price
     */
    @Operation(
            description = "Get all materials for given sales organization number.",
            parameters = {
                    @Parameter(name = "salesOrg", required = true, description = "Sales org to get material for."),
                    @Parameter(name = "salesOffice", description = "Sales office to get material for.", deprecated = true),
                    @Parameter(name = "zone", description = "Zone to get materials for.", deprecated = true),
                    @Parameter(name = "page", required = true, description = "Amount of pages to get", allowEmptyValue = true),
                    @Parameter(name = "pageSize", required = true, description = "Amount for elements per page.", allowEmptyValue = true),
            },
            tags = "CustomerTermsController"
    )
    @GetMapping(path = "/list/{salesOrg}/{salesOffice}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialDTO> getMaterialsWithStdPrice(@PathVariable(value = "salesOrg") String salesOrg,
                                                      @PathVariable(value = "salesOffice", required = false) String salesOffice,
                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "pageSize", defaultValue = "5000") Integer pageSize,
                                                      @RequestParam(value = "zone", required = false) String zone) {
        log.debug("Getting materials for salesOrg: {}", salesOrg);

        return service.getAllMaterialsForSalesOrgBy(salesOrg, page, pageSize);
    }

    /**
     * Get a list of all materials for sales org 100.
     * NB: This is used by the PriceOfferTemplate page to create templates.
     * @return list of Materials.
     */
    @Operation(
            description = "Get a list of all materials for sales org 100.",
            tags = "CustomerTermsController"
    )
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MaterialDTO>>  getAllMaterialsForTemplate() {
        log.debug("Getting all materials for PriceOffer Template");

        List<MaterialDTO> materialDTOS = service.getAllMaterialsForSalesOrgBy("100", 0, 5000);

        if(materialDTOS.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(materialDTOS);
    }
}
