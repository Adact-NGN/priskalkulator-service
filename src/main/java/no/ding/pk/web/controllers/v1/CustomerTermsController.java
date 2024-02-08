package no.ding.pk.web.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.web.dto.v1.web.client.offer.CustomerTermsDTO;
import no.ding.pk.web.handlers.CustomerNotProvidedException;
import no.ding.pk.web.handlers.MissingAgreementStartDateException;
import no.ding.pk.web.handlers.SalesOfficeNotProvidedException;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "CustomerTermsController", description = "Controller for handling customer terms.")
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
    @Operation(summary = "CustomerTerms - Get list of CustomerTermsDTO",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOffice", description = "Comma separated list of sales offices to filter for."),
                    @Parameter(name = "customerNumber", description = "Comma separated list of customer number to filter for."),
                    @Parameter(name = "byTerms", description = "Comma separated list of term types to filter for..")
            },
            tags = "CustomerTermsController"
    )
    @GetMapping("/list")
    public List<CustomerTermsDTO> list(@RequestParam(name = "salesOffice", required = false) String salesOffice,
                                       @RequestParam(name = "customerNumber", required = false) String customerNumber,
                                       @RequestParam(name = "byTerms", required = false) String terms) {
        List<String> customerTermList = new ArrayList<>();

        if(StringUtils.isNotBlank(terms)) {
            customerTermList = Arrays.stream(terms.split(",")).toList();
        }
        List<CustomerTerms> customerTermsList = service.findAll(salesOffice, customerNumber, customerTermList);

        return customerTermsList.stream().map(customerTerms -> modelMapper.map(customerTerms, CustomerTermsDTO.class)).collect(Collectors.toList());
    }

    /**
     * Get list of all active {@code CustomerTerms}
     * @param salesOffice Sales Office to filter for
     * @param customerNumber Customer number to filter for
     * @return List of {@code CustomerTermsDTO}
     */
    @Operation(summary = "CustomerTerms - Get list of all active CustomerTermsDTO for sales office and customer number",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOffice", description = "Sales offices to filter for."),
                    @Parameter(name = "customerNumber", description = "Customer number to filter for."),
            },
            tags = {"CustomerTermsController"}
    )
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
    @Operation(summary = "CustomerTerms - Create new CustomerTerms object.",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, ref = "CustomerTermsDTO"),
            tags = "CustomerTermsController"
    )
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

    @ExceptionHandler({MissingAgreementStartDateException.class})
    public ResponseEntity<Object> handleException() {
        return new ResponseEntity<>("No agreement start date was provided", HttpStatus.BAD_REQUEST);
    }

    /**
     * Update existing customer terms with new values
     * @param id existing customer terms id
     * @param customerTermsDTO updated customer terms values.
     * @return updated customer terms as {@code CustomerTermsDTO}
     */
    @Operation(summary = "CustomerTerms - Update existing customer terms with new values.",
            method = "PUT",
            parameters = {
                    @Parameter(name = "id", description = "Existing customer terms ID.", required = true),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, ref = "CustomerTermsDTO"),
            tags = "CustomerTermsController"
    )
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerTermsDTO save(@PathVariable Long id, @RequestBody CustomerTermsDTO customerTermsDTO) {
        log.debug("Received CustomerTerms: {}", customerTermsDTO);
        log.debug("Updating CustomerTerms...");

        CustomerTerms customerTerms = modelMapper.map(customerTermsDTO, CustomerTerms.class);

        customerTerms = service.update(customerTerms);

        return modelMapper.map(customerTerms, CustomerTermsDTO.class);
    }
}
