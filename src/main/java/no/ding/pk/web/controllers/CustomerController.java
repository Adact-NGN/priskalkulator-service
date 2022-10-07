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
    
    
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> fetchAllCustomers(
    @RequestParam(value = "parentCompany", required = false, defaultValue = "") String parentCompany,
    @RequestParam(value = "customerType", required = false, defaultValue = "Node") String customerType,
    @RequestParam(value = "expand", required = false) String expand,
    @RequestParam(value = "skipToken", required = false) Integer skipToken,
    HttpServletResponse response) {
        return service.fetchCustomersJSON(parentCompany, expand, new ArrayList<String>(Arrays.asList(expand.split(","))), skipToken);
    }    

    @GetMapping(value = "/{knr}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> getCustomerByCustomerNumber(@PathVariable("knr") String knr) {
        return service.findCustomerByCustomerNumber(knr);
    }
}
