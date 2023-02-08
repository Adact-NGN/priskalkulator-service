package no.ding.pk.web.controllers;

import java.util.List;

import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.service.StandardPriceServiceImpl;

@RestController
@RequestMapping("/api/standard-price")
public class StandardPriceController {

    private final Logger log = LoggerFactory.getLogger(StandardPriceController.class);
    
    private StandardPriceServiceImpl service;
    
    @Autowired
    public StandardPriceController(StandardPriceServiceImpl priceService) {
        this.service = priceService;
    }
    
    /**
     * Get a list of Materials with standard price from SAP. Prices is fetch with the combination of Sales Office and Sales Organization.
     * @param salesOffice The sales office to get the prices for.
     * @param salesOrg The sales organization to get the prices for.
     * @return
     */
    @GetMapping(value = "/{salesoffice}/{salesorg}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(@PathVariable("salesoffice") String salesOffice, @PathVariable("salesorg") String salesOrg) {
        List<MaterialStdPriceDTO> materialList = service.getStdPricesForSalesOfficeAndSalesOrg(salesOffice, salesOrg);
        log.debug(String.format("Amount returning: %d", materialList.size()));
        return materialList;
    }
}
