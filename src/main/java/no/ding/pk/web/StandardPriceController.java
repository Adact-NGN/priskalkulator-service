package no.ding.pk.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.service.StandardPriceService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/standard-price")
public class StandardPriceController {
    
    private StandardPriceService service;
    
    @Autowired
    public StandardPriceController(StandardPriceService priceService) {
        this.service = priceService;
    }
    
    @GetMapping(value = "/{salesoffice}/{salesorg}", produces = "application/json")
    public String getStdPricesForSalesOfficeAndSalesOrg(@PathVariable("salesoffice") String salesOffice, @PathVariable("salesorg") String salesOrg) {
        return service.getStdPricesForSalesOfficeAndSalesOrg(salesOffice, salesOrg);
    }
}
