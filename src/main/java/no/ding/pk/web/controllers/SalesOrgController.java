package no.ding.pk.web.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOrgDTO> getAll() {
        return service.getAll();
    }

    /**
     * Search for Sales oraganization by different parameters
     * @param salesOrg number value for sales organization
     * @param salesOffice number value for sales office
     * @param postalCode number value for postal number
     * @param salesZone number value for sales zone
     * @param city string value
     * @param greedy whether to use greedy or un-greedy query, 'or' or 'and'. Default greedy (true): or
     * @return List of sales organizations, else empty list
     */
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
    @GetMapping(path = "/{salesOrg}/{salesOffice}/zones", produces = MediaType.APPLICATION_JSON_VALUE)
    List<ZoneDTO> getZonesForSalesOffice(
            @PathVariable("salesOrg") String salesOrg,
            @PathVariable("salesOffice") String salesOffice,
            @RequestParam(value = "postalCode", required = false) String postalCode) {
        List<String> params = List.of(salesOrg, salesOffice, "");

        List<SalesOrgField> fieldList = List
                .of(SalesOrgField.SalesOrganization,
                        SalesOrgField.SalesOffice,
                        SalesOrgField.SalesZone);

        StringBuilder queryBuilder = new StringBuilder();

        String logicDivider = " and ";

        for(int i = 0; i < params.size(); i++) {
            String param = params.get(i);

            if(StringUtils.isNotBlank(param)) {
                String fieldType = fieldList.get(i).getType();

                if(Objects.equals(fieldList.get(i).getName(), SalesOrgField.City.getName())) {
                    param = param.toUpperCase();
                }

                log.debug("Parameter: {} fieldType: {}", param, fieldType);

                if(StringUtils.equals(fieldType, "numeric") && !StringUtils.isNumeric(param)) {
                    continue;
                }

                log.debug("Parameter ({}) and parameter type ({}) matches. Add to query.", param, fieldType);

                String field = fieldList.get(i).getName();
                addAndToQuery(queryBuilder, logicDivider);

                String comparator = " eq ";

                if(fieldList.get(i) == SalesOrgField.SalesZone) {
                    comparator = " ne ";
                }
                queryBuilder.append(field).append(comparator).append(String.format("'%s'", param));
            }
        }

        List<SalesOrgDTO> salesOrgDTOList = service.findByQuery(queryBuilder.toString(), 0);

        salesOrgDTOList.sort(Comparator.comparing(SalesOrgDTO::getSalesZone));

        SalesOrgDTO standardZone = getStandardZoneForPostalCode(postalCode, salesOrgDTOList);

        if(standardZone != null) {
            salesOrgDTOList.add(0, standardZone);
        }

        Map<String, SalesOrgDTO> distinctSalesOrgMap = new TreeMap<>();
        salesOrgDTOList.forEach(salesOrgDTO -> {
            if(StringUtils.isNotBlank(salesOrgDTO.getSalesZone()) && !distinctSalesOrgMap.containsKey(salesOrgDTO.getSalesZone())) {
                distinctSalesOrgMap.put(salesOrgDTO.getSalesZone(), salesOrgDTO);
            }
        });
        return distinctSalesOrgMap.values().stream().map(data -> {
            boolean isStandardZone = standardZone != null && StringUtils.isNotBlank(data.getSalesZone()) && StringUtils.equals(data.getSalesZone(), standardZone.getSalesZone());
            return ZoneDTO.builder()
                    .isStandardZone(isStandardZone)
                    .postalCode(data.getPostalCode())
                    .zoneId(data.getSalesZone().replace("0", ""))
                    .postalName(data.getCity()).build();
        }).collect(Collectors.toList());
    }

    private SalesOrgDTO getStandardZoneForPostalCode(String postalCode, List<SalesOrgDTO> salesOrgDTOList) {
        if(salesOrgDTOList.isEmpty()) {
            return null;
        }

        List<SalesOrgDTO> standardZones = salesOrgDTOList.stream().filter(salesOrgDTO -> salesOrgDTO.getPostalCode().equals(postalCode)).collect(Collectors.toList());

        if(standardZones.size() > 1) {
            standardZones = standardZones.stream().filter(salesOrgDTO -> StringUtils.isNotBlank(salesOrgDTO.getSalesZone())).collect(Collectors.toList());
        }

        if(!standardZones.isEmpty()) {
            return standardZones.get(0);
        }

        return null;
    }

    private void addAndToQuery(StringBuilder queryBuilder, String logicDivider) {
        if(!queryBuilder.isEmpty()) {
            queryBuilder.append(logicDivider);
        }
    }
}
