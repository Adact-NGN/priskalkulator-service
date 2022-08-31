package no.ding.pk.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.service.CustomerService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    
    @GetMapping(value = "/fetch-all", produces = "application/json")
    public String fetchAllCustomers(HttpServletResponse response) {
        // response.addHeader("Access-Control-Allow-Origin", "*");
        // response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,DELETE,PUT");
        return service.fetchCustomersJSON();
    }    
}
