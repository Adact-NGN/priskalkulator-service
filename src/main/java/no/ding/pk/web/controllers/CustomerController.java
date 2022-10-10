package no.ding.pk.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.service.CustomerServiceImpl;
import no.ding.pk.web.dto.CustomerDTO;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    private CustomerServiceImpl service;
    
    @Autowired
    public CustomerController(CustomerServiceImpl service) {
        this.service = service;
    }
    
    /**
     * Get all customers by parent company (SalesOrg).
     * @param parentCompany (<i>Required</i>) Parent company number (SalesOrg number), ex 100
     * @param customerType (<i>Optional</i>) Customer type to filter for, ex Node, Betaler etc.
     * @param expand (<i>Optional</i>) Comma separated list of fields in the object to expand.
     * @param skipToken (<i>Optional</i>) Amount of items to skip for this request
     * @return List of customer object, else empty list.
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> fetchAllCustomers(
    @RequestParam(value = "parentCompany") String parentCompany,
    @RequestParam(value = "customerType", required = false, defaultValue = "Betaler") String customerType,
    @RequestParam(value = "expand", required = false) String expand,
    @RequestParam(value = "skipToken", required = false) Integer skipToken) {
        return service.fetchCustomersJSON(parentCompany, 
        customerType, 
        new ArrayList<String>(Arrays.asList(expand.split(","))), 
        skipToken);
    }    

    /**
     * Get customer by customer number.
     * @param knr Customer number to search for.
     * @return List with single customer object, else empty list.
     */
    @GetMapping(value = "/{knr}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> getCustomerByCustomerNumber(@PathVariable("knr") String knr) {
        return service.findCustomerByCustomerNumber(knr);
    }
}
