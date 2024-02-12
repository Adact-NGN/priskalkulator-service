package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.service.sap.CustomerService;
import no.ding.pk.web.dto.sap.CustomerDTO;
import no.ding.pk.web.enums.SapCustomerField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag(name = "CustomerController", description = "Get Customer data from SAP")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    
    private final CustomerService service;
    
    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }
    
    /**
     * Get all customers by parent company (SalesOrg).
     * @param parentCompany (<i>Required</i>) Parent company number (SalesOrg number), e.g. 100
     * @param customerType (<i>Optional</i>) Customer type to filter for, ex Node, Betaler etc.
     * @param expand (<i>Optional</i>) Comma separated list of fields in the object to expand.
     * <ul>
     * <li>Node</li>
     * <li>Betaler</li>
     * </ul>
     * @param skipToken (<i>Optional</i>) Amount of items to skip for this request
     * @return List of customer object, else empty list.
     */
    @Operation(summary = "Customer - Get all customers by parent company (Sales org)",
            description = "Get all customers by parent company (SalesOrg).",
          method = "GET",
            parameters = {
                    @Parameter(name = "parentCompany", description = "Parent company number (SalesIrg number), e.g. 100", required = true),
                    @Parameter(name = "customerType", description = "Customer type to filter for, e.g. Node, Betaler etc."),
                    @Parameter(name = "expand", description = "Comma separated list of fields in the object to expand."),
                    @Parameter(name = "skipToken", description = "Amount of items to skip for this request.")
            },
            tags = "CustomerController"
    )
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> fetchAllCustomers(
    @RequestParam(value = "parentCompany") String parentCompany,
    @RequestParam(value = "customerType", required = false, defaultValue = "Betaler") String customerType,
    @RequestParam(value = "expand", required = false) String expand,
    @RequestParam(value = "skipToken", required = false) Integer skipToken) {

        log.debug(String.format("Request received with params: %s", "parentCompany=" + parentCompany +", customerType=" + customerType +
        ", expand=" + expand + ", skipToken=" + skipToken));

        if(StringUtils.isBlank(parentCompany)) {
            return new ArrayList<>();
        }

        List<String> expansionFields = new ArrayList<>();
        if(expand != null) {
            expansionFields.addAll(Arrays.asList(expand.split(",")));
        }

        return service.fetchCustomersJSON(parentCompany, 
        customerType, 
        expansionFields, 
        skipToken);
    }    

    /**
     * Get customer by customer number.
     * @param knr Customer number to search for.
     * @return List with single customer object, else empty list.
     */
    @Operation(
            summary = "Customer - Get customer by customer number",
            description = "Get customer by customer number.",
            method = "GET",
            parameters = @Parameter(name = "knr", description = "Customer number to search for.", required = true),
            tags = "CustomerController"
    )
    @GetMapping(path = "/{knr}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> getCustomerByCustomerNumber(@PathVariable("knr") String knr) {
        log.debug("Request received with params: knr=" + knr);
        return service.findCustomerByCustomerNumber(knr);
    }

    /**
     * Search for customer by partial name or number.
     * @param salesOrg Which sales organization to search in
     * @param searchString Search string containing either part of the customer number or name
     * @return List of customer object, else empty list
     */
    @Operation(
            summary = "Customer - Search for customer",
            description = "Search for customer by partial name or number.",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOrg", description = "Which sales organization to search in.", required = true),
                    @Parameter(name = "searchString", description = "Search string containing either part of the customer number or name.")
            },
            tags = "CustomerController"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> searchForCustomer(@RequestParam("salesOrg") String salesOrg, @RequestParam("searchString") String searchString) {
        log.debug(String.format("Getting search request with salesOrg: '%s' and searchString: '%s'", salesOrg, searchString));
        if(!StringUtils.isBlank(searchString)) {
            if(NumberUtils.isDigits(searchString)) {
                return service.searchCustomerBy(salesOrg, SapCustomerField.Kundenummer.getValue(), searchString);
            } else {
                return service.searchCustomerBy(salesOrg, SapCustomerField.Navn1.getValue(), searchString);
            }
        }
        return new ArrayList<>();
    }
}
