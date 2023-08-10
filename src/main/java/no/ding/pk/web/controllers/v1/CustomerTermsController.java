package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.web.dto.v1.web.client.offer.CustomerTermsDTO;
import no.ding.pk.web.handlers.CustomerNotProvidedException;
import no.ding.pk.web.handlers.MissingAgreementStartDateException;
import no.ding.pk.web.handlers.SalesOfficeNotProvidedException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Get list of {@code CustomerTerms}
     * @param salesOffice Sales office to filter for, { {@code @required}  false } .
     * @param customerNumber customer number to filter for, { {@code @required}  false } .
     * @return List of {@code CustomerTermsDTO}
     */
    @GetMapping("/list")
    public List<CustomerTermsDTO> list(@RequestParam(name = "salesOffice", required = false) String salesOffice,
                                       @RequestParam(name = "customerNumber", required = false) String customerNumber) {
        List<CustomerTerms> customerTermsList = service.findAll(salesOffice, customerNumber);

        return customerTermsList.stream().map(customerTerms -> modelMapper.map(customerTerms, CustomerTermsDTO.class)).collect(Collectors.toList());
    }

    /**
     * Get list of all active {@code CustomerTerms}
     * @param salesOffice Sales Office to filter for
     * @param customerNumber Customer number to filter for
     * @return List of {@code CustomerTermsDTO}
     */
    @GetMapping("/list/active")
    public List<CustomerTermsDTO> listAllActive(@RequestParam(name = "salesOffice", required = false) String salesOffice,
                                                @RequestParam(name = "customerNumber", required = false) String customerNumber) {
        List<CustomerTerms> customerTermsList = service.findAllActive(salesOffice, customerNumber);

        return customerTermsList.stream().map(customerTerms -> modelMapper.map(customerTerms, CustomerTermsDTO.class)).collect(Collectors.toList());
    }

    /**
     * Create new {@code CustomerTerms} object
     * @param customerTermsDTO Customer terms object tot persist.
     * @return Newly persisted customer terms as {@code CustomerTermsDTO}
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerTermsDTO create(@RequestBody CustomerTermsDTO customerTermsDTO) {

        log.debug("Received CustomerTerms: {}", customerTermsDTO);

        if(customerTermsDTO.getSalesOffice() == null) throw new SalesOfficeNotProvidedException();
        if(customerTermsDTO.getCustomerNumber() == null) throw new CustomerNotProvidedException("Customer number is missing");
        if(customerTermsDTO.getCustomerName() == null) throw new CustomerNotProvidedException("Customer name is missing");
        if(customerTermsDTO.getAgreementStartDate() == null) throw new MissingAgreementStartDateException();

        CustomerTerms customerTerms = modelMapper.map(customerTermsDTO, CustomerTerms.class);

        customerTerms = service.save(customerTerms);

        return modelMapper.map(customerTerms, CustomerTermsDTO.class);
    }

    /**
     * Update existing customer terms with new values
     * @param id existing customer terms id
     * @param customerTermsDTO updated customer terms values.
     * @return updated customer terms as {@code CustomerTermsDTO}
     */
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerTermsDTO save(@PathVariable Long id, @RequestBody CustomerTermsDTO customerTermsDTO) {
        log.debug("Received CustomerTerms: {}", customerTermsDTO);
        log.debug("Updating CustomerTerms...");

        CustomerTerms customerTerms = modelMapper.map(customerTermsDTO, CustomerTerms.class);

        customerTerms = service.update(customerTerms);

        return modelMapper.map(customerTerms, CustomerTermsDTO.class);
    }
}
