package no.ding.pk.web.controllers;

import no.ding.pk.service.sap.SalesOrgService;
import no.ding.pk.web.dto.sap.SalesOrgDTO;
import no.ding.pk.web.enums.SalesOrgField;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
     * @param greedy wheter to use greedy or ungreedy query, 'or' or 'and'. Default greedy (true): or
     * @return List of sales organizations, else empty list
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOrgDTO> findByRequestParams(
            @RequestParam(name = "salesOrg", required = false) String salesOrg,
            @RequestParam(name = "salesOffice", required = false) String salesOffice,
            @RequestParam(name = "postalCode", required = false) String postalCode,
            @RequestParam(name = "salesZone", required = false) String salesZone,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "skiptokens", required = false) Integer skipTokens,
            @RequestParam(name = "greedy", required = false, defaultValue = "true") String greedy
    ) {
        String[] paramsList;
        if(skipTokens != null) {
            paramsList = new String[]{salesOrg, salesOffice, postalCode, salesZone, city, Integer.toString(skipTokens)};
        } else {
            paramsList = new String[]{salesOrg, salesOffice, postalCode, salesZone, city};
        }

        List<SalesOrgField> fieldList = SalesOrgField.fieldList();

        boolean isAllBlank = true;
        for(String param : paramsList) {
            if(!StringUtils.isBlank(param)) {
                isAllBlank = false;
            }
        }

        if(isAllBlank) {
            log.debug("All request parameters where blank.");
            return new ArrayList<>();
        } else {
            log.debug("Got the parameters: " + Arrays.toString(paramsList));
        }

        StringBuilder queryBuilder = new StringBuilder();

        String logicDivider;
        if(BooleanUtils.toBoolean(greedy)) {
            logicDivider = " or ";
        } else {
            logicDivider = " and ";
        }

        for(int i = 0; i < paramsList.length && i < fieldList.size(); i++) {
            String param = paramsList[i];

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

                queryBuilder.append(field).append(" eq ").append(String.format("'%s'", param));
            }
        }

        log.debug("Calling service with query: " + queryBuilder);

        return service.findByQuery(queryBuilder.toString(), skipTokens);
    }

    @GetMapping("/{salesOrg}/{salesOffice}/zones")
    List<SalesOrgDTO> getZonesForSalesOffice(
            @PathVariable("salesOrg") String salesOrg,
            @PathVariable("salesOffice") String salesOffice) {
        List<String> params = List
                .of(salesOrg, salesOffice, "");

        List<SalesOrgField> fieldList = List
                .of(SalesOrgField.SalesOrganization,
                        SalesOrgField.SalesOffice,
                        SalesOrgField.SalesZone);

        StringBuilder queryBuilder = new StringBuilder();

        String logicDivider = " and ";

        for(int i = 0; i < params.size(); i++) {
            String param = params.get(i);

            if(param != null) {
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

        Map<String, SalesOrgDTO> distinctSalesOrgMap = new TreeMap<>();
        salesOrgDTOList.forEach(salesOrgDTO -> {
            if(StringUtils.isNotBlank(salesOrgDTO.getSalesZone()) && !distinctSalesOrgMap.containsKey(salesOrgDTO.getSalesZone())) {
                distinctSalesOrgMap.put(salesOrgDTO.getSalesZone(), salesOrgDTO);
            }
        });
        return List.copyOf(distinctSalesOrgMap.values());
    }

    private void addAndToQuery(StringBuilder queryBuilder, String logicDivider) {
        if(queryBuilder.length() > 0) {
            queryBuilder.append(logicDivider);
        }
    }
}
