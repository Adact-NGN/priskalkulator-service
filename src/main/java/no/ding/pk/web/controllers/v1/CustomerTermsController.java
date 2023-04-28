package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.web.dto.v1.web.client.offer.CustomerTermsDTO;
import no.ding.pk.web.handlers.CustomerNotProvidedException;
import no.ding.pk.web.handlers.SalesOfficeNotProvidedException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/terms/customer")
public class CustomerTermsController {

    private static final Logger log = LoggerFactory.getLogger(CustomerTermsController.class);

    private final CustomerTermsService service;

    private final ModelMapper modelMapper;

    @Autowired
    public CustomerTermsController(CustomerTermsService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/list")
    public List<CustomerTermsDTO> list() {
        List<CustomerTerms> customerTermsList = service.findAll();

        return customerTermsList.stream().map(customerTerms -> modelMapper.map(customerTerms, CustomerTermsDTO.class)).collect(Collectors.toList());
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerTermsDTO create(@RequestBody CustomerTermsDTO customerTermsDTO) {

        log.debug("Received CustomerTerms: {}", customerTermsDTO);

        if(customerTermsDTO.getSalesOffice() == null) throw new SalesOfficeNotProvidedException();
        if(customerTermsDTO.getCustomerNumber() == null) throw new CustomerNotProvidedException();

        CustomerTerms customerTerms = modelMapper.map(customerTermsDTO, CustomerTerms.class);

        customerTerms = service.save(customerTerms.getSalesOffice(), customerTerms.getCustomerNumber(), customerTerms);

        return modelMapper.map(customerTerms, CustomerTermsDTO.class);
    }

    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerTermsDTO save(@PathVariable Long id, @RequestBody CustomerTermsDTO customerTermsDTO) {
        log.debug("Received CustomerTerms: {}", customerTermsDTO);
        log.debug("Updating CustomerTerms...");

        CustomerTerms customerTerms = modelMapper.map(customerTermsDTO, CustomerTerms.class);

        customerTerms = service.update(customerTerms);

        return modelMapper.map(customerTerms, CustomerTermsDTO.class);
    }

    @GetMapping("/new")
    public CustomerTermsDTO newCustomerTermsDTO() {
        return new CustomerTermsDTO();
    }
    
}
