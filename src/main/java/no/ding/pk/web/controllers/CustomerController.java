package no.ding.pk.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.service.CustomerServiceImpl;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private CustomerServiceImpl service;

    @Autowired
    public CustomerController(CustomerServiceImpl service) {
        this.service = service;
    }

    
    @GetMapping(value = "/list", produces = "application/json")
    public String fetchAllCustomers(HttpServletResponse response) {
        // response.addHeader("Access-Control-Allow-Origin", "*");
        // response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,DELETE,PUT");
        return service.fetchCustomersJSON();
    }    
}
