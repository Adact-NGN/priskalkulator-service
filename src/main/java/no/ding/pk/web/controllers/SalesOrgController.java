package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.service.sap.SalesOrgService;
import no.ding.pk.web.dto.sap.SalesOrgDTO;
import no.ding.pk.web.dto.v1.web.client.ZoneDTO;
import no.ding.pk.web.enums.SalesOrgField;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "SalesOrganizationController", description = "Get sales organisation information.")
@RestController
@RequestMapping(value = {"/api/salesorg", "/api/v1/salesorg"})
public class SalesOrgController {

    private static final Logger log = LoggerFactory.getLogger(SalesOrgController.class);

    private final SalesOrgService service;

    @Autowired
    public SalesOrgController(SalesOrgService service) {
        this.service = service;
    }

    /**
     * Gets all sales organizations
     * @return List of sales organizations, else empty list
     */
    @Operation(description = "Get all sales organizations.",
            method = "GET",
            tags = "SalesOrganizationController"
    )
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOrgDTO> getAll() {
        return service.getAll();
    }

    /**
     * Search for Sales organization by different parameters
     * @param salesOrg number value for sales organization
     * @param salesOffice number value for sales office
     * @param postalCode number value for postal number
     * @param salesZone number value for sales zone
     * @param city string value
     * @param greedy whether to use greedy or un-greedy query, 'or' or 'and'. Default greedy (true): or
     * @return List of sales organizations, else empty list
     */
    @Operation(description = "Search for Sales organization by different parameters",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales organization number. E.g. 100"),
                    @Parameter(name = "salesOffice", description = "Sales office number. E.g. 104"),
                    @Parameter(name = "postalCode", description = "postal code number. E.g. 0100"),
                    @Parameter(name = "salesZone", description = "Sales zone number. E.g. 3"),
                    @Parameter(name = "city", description = "Sales organization number. E.g. oslo"),
                    @Parameter(name = "skiptokens", description = "Amount of items to skip. E.g. 0 for no skip, 100 to skip 100 items. Default 0"),
                    @Parameter(name = "greedy", description = "Whether to use greedy or un-greedy query, 'or' or 'and'. Default greedy (true): or"),
            },
            tags = "SalesOrganizationController"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOrgDTO> findByRequestParams(
            @RequestParam(name = "salesOrg", required = false) String salesOrg,
            @RequestParam(name = "salesOffice", required = false) String salesOffice,
            @RequestParam(name = "postalCode", required = false) String postalCode,
            @RequestParam(name = "salesZone", required = false) String salesZone,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "skiptokens", required = false, defaultValue = "0") Integer skipTokens,
            @RequestParam(name = "greedy", required = false, defaultValue = "true") Boolean greedy
    ) {
        log.debug("Searching for Sales org entity with parameters:");
        log.debug("salesOrg: {}, salesOffice: {}, postalCode: {}, salesZone: {}, city: {}, greedy: {}",
                salesOrg, salesOffice, postalCode, salesZone, city, greedy);
        Map<SalesOrgField, String> params = createParameterList(salesOrg, salesOffice, postalCode, salesZone, city);

        boolean isAllBlank = true;
        for(String param : params.values()) {
            if(!StringUtils.isBlank(param)) {
                isAllBlank = false;
            }
        }

        if(isAllBlank) {
            log.debug("All request parameters where blank.");
            return new ArrayList<>();
        } else {
            log.debug("Got the parameters: " + params);
        }

        StringBuilder queryBuilder = new StringBuilder();

        String logicDivider;
        if(BooleanUtils.toBoolean(greedy)) {
            logicDivider = " or ";
        } else {
            logicDivider = " and ";
        }

        for (Map.Entry<SalesOrgField, String> entry : params.entrySet()) {
            String param = entry.getValue();

            if(StringUtils.isNotBlank(param)) {
                String fieldType = entry.getKey().getType();

                if(Objects.equals(entry.getKey().getName(), SalesOrgField.City.getName())) {
                    param = param.toUpperCase();
                }

                log.debug("Parameter: {} fieldType: {}", param, fieldType);

                if(StringUtils.equals(fieldType, "numeric") && !StringUtils.isNumeric(param)) {
                    continue;
                }

                log.debug("Parameter ({}) and parameter type ({}) matches. Add to query.", param, fieldType);

                String field = entry.getKey().getName();
                addAndToQuery(queryBuilder, logicDivider);

                queryBuilder.append(field).append(" eq ").append(String.format("'%s'", param));
            }
        }

        log.debug("Calling service with query: " + queryBuilder);

        return service.findByQuery(queryBuilder.toString(), skipTokens);
    }

    private void addAndToQuery(StringBuilder queryBuilder, String logicDivider) {
        if(!queryBuilder.isEmpty()) {
            queryBuilder.append(logicDivider);
        }
    }

    private Map<SalesOrgField, String> createParameterList(String... params) {
        Map<SalesOrgField, String> returnMap = new LinkedHashMap<>();
        for(int i = 0; i < params.length; i++) {
            SalesOrgField field = SalesOrgField.fieldList().get(i);

            returnMap.put(field, params[i]);
        }

        return returnMap;
    }

    /**
     * Get all zones for a Sales office
     * @param salesOrg Sales organization for the sales office
     * @param salesOffice Sales office number to lookup zones for.
     * @param postalCode Set postal code to get the standard zone.
     * @return A list of ZoneDTO, else empty list
     */
    @Operation(description = "Get all zones for a Sales office",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Sales organization for too look into.", deprecated = true),
                    @Parameter(name = "salesOffice", description = "Sales office number to look up zones for.", required = true),
                    @Parameter(name = "postalCode", description = "Set postal code to get the standard zone.")
            },
            tags = "SalesOrgController"
    )
    @GetMapping(path = "/{salesOrg}/{salesOffice}/zones", produces = MediaType.APPLICATION_JSON_VALUE)
    List<ZoneDTO> getZonesForSalesOffice(
            @PathVariable("salesOrg") String salesOrg,
            @PathVariable("salesOffice") String salesOffice,
            @RequestParam(value = "postalCode", required = false) String postalCode) {
        return service.getZonesForSalesOffice(salesOffice, postalCode);
    }
}
