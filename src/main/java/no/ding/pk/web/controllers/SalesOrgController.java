package no.ding.pk.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.service.SalesOrgService;
import no.ding.pk.web.dto.SalesOrgDTO;
import no.ding.pk.web.enums.SalesOrgField;

@RestController
@RequestMapping("/api/salesorg")
public class SalesOrgController {

    private static final Logger log = LoggerFactory.getLogger(SalesOrgController.class);

    private SalesOrgService service;
    
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
     * @param postalNumber number value for postal number
     * @param salesZone number value for sales zone
     * @param city string value
     * @param greedy wheter to use greedy or ungreedy query, 'or' or 'and'. Default greedy (true): or
     * @return List of sales organizations, else empty list
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOrgDTO> findByRequestParams(
    @RequestParam(name = "salesOrg", required = false) String salesOrg,
    @RequestParam(name = "salesOffice", required = false) String salesOffice,
    @RequestParam(name = "postalNumber", required = false) String postalNumber,
    @RequestParam(name = "salesZone", required = false) String salesZone,
    @RequestParam(name = "city", required = false) String city,
    @RequestParam(name = "skiptokens", required = false) Integer skipTokens,
    @RequestParam(name = "greedy", required = false, defaultValue = "true") String greedy
    ) {
        String[] paramsList;
        if(skipTokens != null) {
            paramsList = new String[]{salesOrg, salesOffice, postalNumber, salesZone, city, Integer.toString(skipTokens)};
        } else {
            paramsList = new String[]{salesOrg, salesOffice, postalNumber, salesZone, city};
        }

        
        boolean isAllBlank = true;
        for(String param : paramsList) {
            if(!StringUtils.isBlank(param)) {
                isAllBlank = false;
                break;
            }
        }

        if(isAllBlank) {
            return new ArrayList<>();
        }

        List<String> fieldList = SalesOrgField.fieldList();

        StringBuilder queryBuilder = new StringBuilder();

        String logicDivider;
        if(BooleanUtils.toBoolean(greedy)) {
            logicDivider = " or ";
        } else {
            logicDivider = " and ";
        }

        for(int i = 0; i < paramsList.length; i++) {
            String param = paramsList[i];

            if(StringUtils.isNotBlank(param)) {
                addAndToQuery(queryBuilder, logicDivider);
                String field = fieldList.get(i);
                queryBuilder.append(field).append(" eq ").append(String.format("'%s'", param));
            }
        }

        log.debug("Calling service with query: " + queryBuilder.toString());

        return service.findByQuery(queryBuilder.toString(), skipTokens);
    }

    private void addAndToQuery(StringBuilder queryBuilder, String logicDivider) {
        if(queryBuilder.length() > 0) {
            queryBuilder.append(logicDivider);
        }
    }
}
