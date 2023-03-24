package no.ding.pk.web.controllers;

import no.ding.pk.service.sap.StandardPriceServiceImpl;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/standard-price", "/api/v1/standard-price"})
public class StandardPriceController {

    private final Logger log = LoggerFactory.getLogger(StandardPriceController.class);
    
    private final StandardPriceServiceImpl service;
    
    @Autowired
    public StandardPriceController(StandardPriceServiceImpl priceService) {
        this.service = priceService;
    }
    
    /**
     * Get a list of Materials with standard price from SAP. Prices is fetch with the combination of Sales Office and Sales Organization.
     * @param salesOrg The sales organization to get the prices for.
     * @param salesOffice The sales office to get the prices for.
     * @param zone Set the zone number for only getting Material prices for a specific zone. @required
     * @return list of MaterialStdPriceDTO objects
     */
    @GetMapping(value = "/{salesorg}/{salesoffice}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(
            @PathVariable("salesorg") String salesOrg,
            @PathVariable("salesoffice") String salesOffice,
            @RequestParam(value = "zone", required = false) String zone
    ) {
        List<MaterialStdPriceDTO> materialList = service.getStdPricesForSalesOfficeAndSalesOrg(salesOffice, salesOrg, zone);

        log.debug(String.format("Amount returning: %d", materialList.size()));
        return materialList;
    }

    @GetMapping(value = "/{salesorg}/{salesoffice}/{material}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialStdPriceDTO> getStdPriceForSalesOrgAndSalesOfficeAndMaterial(
            @PathVariable("salesorg") String salesOrg,
            @PathVariable("salesoffice") String salesOffice,
            @PathVariable("material") String material,
            @RequestParam(value = "zone", required = false) String zone
    ) {
        return service.getStandardPriceForSalesOrgSalesOfficeAndMaterial(salesOrg, salesOffice, material, zone);
    }
}
