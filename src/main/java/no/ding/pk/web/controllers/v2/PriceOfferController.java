package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTerms;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferListDTO;
import no.ding.pk.web.dto.web.client.requests.ActivatePriceOfferRequest;
import no.ding.pk.web.dto.web.client.requests.ApprovalRequest;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.handlers.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Price Offer Controller V2", description = "Controller for handling price offers.")
@RestController(value = "priceOfferControllerV2")
@RequestMapping("/api/v2/price-offer")
public class PriceOfferController {
    private static final Logger log = LoggerFactory.getLogger(PriceOfferController.class);
    
    private final PriceOfferService service;

    private final ModelMapper modelMapper;
    
    @Autowired
    public PriceOfferController(
            PriceOfferService service,
            @Qualifier(value = "modelMapperV2") ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }
    
    /**
     * Get all price offers
     * @param statuses list of all price offer with statuses to be inn the list.
     * @return List of price offers, else empty list
     */
    @Operation(summary = "Get all price offers",
            method = "GET",
            parameters = {
                    @Parameter(name = "statuses", description = "Comma separated list of price offer statuses to filter on.")
            },
            tags = "priceOfferControllerV2"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of PriceOfferDTOList objects.")
    })
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferListDTO> list(@RequestParam(value = "statuses", required = false) String statuses) {

        List<PriceOffer> priceOfferList;
        if(StringUtils.isNotBlank(statuses)) {
            List<String> statusList = new ArrayList<>(Arrays.stream(statuses.split(",")).toList());
            priceOfferList = service.findAllByPriceOfferStatusInList(statusList);
        } else {
            priceOfferList = service.findAllWithoutStatusInList(List.of(PriceOfferStatus.ACTIVATED.getStatus()));
        }

        if(!priceOfferList.isEmpty()) {
            return priceOfferList.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferListDTO.class)).collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Get all price offers created by sales employee
     * @param salesEmployeeId User id to list price offers for.
     * @return list of price offers connected to sales employee, else empty list.
     */
    @Operation(summary = "Get all price offers created by sales employee",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesEmployeeId", description = "User id to list price offers for.", required = true),
                    @Parameter(name = "statuses", description = "Comma separated list of price offer statuses to filter on.")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of PriceOfferDTOList objects.")
    })
    @GetMapping(path = "/list/{salesEmployeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferListDTO> listBySalesEmployee(@PathVariable("salesEmployeeId") Long salesEmployeeId,
                                                       @RequestParam(value = "statuses", required = false) String statuses) {
        List<PriceOffer> priceOffers;

        if(StringUtils.isNotBlank(statuses)) {
            List<String> statusList = Arrays.stream(statuses.split(",")).toList();
            priceOffers = service.findAllBySalesEmployeeId(salesEmployeeId, statusList);
        } else {
            priceOffers = service.findAllBySalesEmployeeId(salesEmployeeId, null);
        }
        
        if(!priceOffers.isEmpty()) {
            return priceOffers.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferListDTO.class)).collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }

    @Operation(summary = "List Price offers with filtering by sales office number and by status.",
            method = "GET",
            parameters = {
                    @Parameter(name = "offices", required = true, description = "Comma separated list of sales office numbers", example = "100,101,102"),
                    @Parameter(name = "statuses", description = "Comma separated list of price offer status to filter for.", example = "APPROVED,PENDING")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A list of price offers filtered on sales office numbers and, if given, a list of statuses.")
    })
    @GetMapping(path = "/list/sales-offices")
    public ResponseEntity<List<PriceOfferListDTO>> listAllBySalesOffice(@RequestParam("offices") String salesOffices,
                                                                        @RequestParam(value = "statuses", required = false) String statuses) {
        if(StringUtils.isBlank(salesOffices)) {
            throw new BadRequestException("Missing comma separated list of sales office numbers.");
        }
        List<String> salesOfficesList = Arrays.stream(salesOffices.split(",")).toList();
        List<String> statusList = StringUtils.isNotBlank(statuses) ? Arrays.stream(statuses.split(",")).toList() : null;

        List<PriceOffer> priceOffers = service.findAllBySalesOfficeAndStatus(salesOfficesList, statusList);

        if(!priceOffers.isEmpty()) {
            List<PriceOfferListDTO> offerListDTOS = priceOffers.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferListDTO.class)).collect(Collectors.toList());
            return new ResponseEntity<>(offerListDTOS, HttpStatus.OK);
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK) ;
    }

    /**
     * Activate price offer
     * @param activatedById id for {@code User}
     * @param priceOfferId id for price offer
     * @param activatePriceOfferRequest request object with completed customer terms to be added to the price offer.
     * @return true if price offer is updated, else false
     */
    @Operation(summary = "Activate price offer",
            method = "PUT",
            parameters = {
                    @Parameter(name = "activatedById", description = "ID of the user the offer is being activated by."),
                    @Parameter(name = "priceOfferId", description = "ID for the price offer being activated."),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request body must contain updated contract terms. Optionally users can add a general comment to the priceing team.")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns value true or false depending on if the offer was activated or not.")
    })
    @PutMapping(path = "/activate/{activatedById}/{priceOfferId}")
    public ResponseEntity<Boolean> activatePriceOffer(@PathVariable("activatedById") Long activatedById,
                                      @PathVariable("priceOfferId") Long priceOfferId,
                                      @RequestBody ActivatePriceOfferRequest activatePriceOfferRequest) {
        log.debug("Price offer with id {} is activated by user with id {}", priceOfferId, activatedById);

        if(activatePriceOfferRequest.getTerms() == null) {
            throw new MissingTermsInRequestPayloadException();
        }

        PriceOfferTerms customerTerms = modelMapper.map(activatePriceOfferRequest.getTerms(), PriceOfferTerms.class);
        Boolean activated = service.activatePriceOffer(activatedById, priceOfferId, customerTerms, activatePriceOfferRequest.getGeneralComment());
        return ResponseEntity.ok(activated);
    }
    
    /**
     * Set approval status for price offer
     * @param approverId Approver User id
     * @param priceOfferId ID for Price offer to be approved
     * @param approvalRequest Approval status
     * @return True if price offer was successfully approved, else false.
     */
    @Operation(summary = "Approve price offer",
            method = "PUT",
            parameters = {
                    @Parameter(name = "approverId",
                            description = "The user id to the user which is approving this price offer.",
                            required = true),
                    @Parameter(name = "priceOfferId", description = "The id to the price offer to approve.")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request body containing the status to be set and a comment with additional information.", ref = "ApprovalRequest")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns true if the price offer was able to be approved, else false.")
    })
    @PutMapping(path = "/approval/{approverId}/{priceOfferId}")
    public Boolean priceOfferApproval(@PathVariable("approverId") Long approverId,
                                      @PathVariable("priceOfferId") Long priceOfferId,
                                      @RequestBody ApprovalRequest approvalRequest) {
        log.debug("Approval request received with request body: {}", approvalRequest);
        return service.approvePriceOffer(priceOfferId, approverId, approvalRequest.getStatus(), approvalRequest.getAdditionalInformation());
    }
    
    /**
     * Find and filter all price offers for approver.
     * @param approverId approver user id
     * @param priceOfferStatus price offer status to filter on {@code not required}
     * @return List of price offers for approver
     */
    @Operation(summary = "Find and filter all price offers for approver",
            method = "GET",
            parameters = {
                    @Parameter(name = "approverId", description = "Approver user ID", required = true),
                    @Parameter(name = "priceOfferStatus", description = "Price offer status to filter on")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns a list of PriceOfferList objects.")
    })
    @GetMapping(path = "/list/approver/{approverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferListDTO> listByApprover(@PathVariable("approverId") Long approverId,
                                              @RequestParam(value = "status", required = false) String priceOfferStatus) {
        List<PriceOffer> priceOffers = service.findAllByApproverIdAndPriceOfferStatus(approverId, priceOfferStatus);

        if(!priceOffers.isEmpty()) {
            log.debug("Found price offers, {}, for approver", priceOffers.size());
            PriceOfferDTO[] priceOfferDTOS = modelMapper.map(priceOffers, PriceOfferDTO[].class);
            log.debug("Mapped {} amount", priceOfferDTOS.length);
            return Arrays.stream(priceOfferDTOS).map(priceOffer -> modelMapper.map(priceOffer, PriceOfferListDTO.class)).toList();
        }

        log.debug("No price offers for approver {} was found.", approverId);

        return new ArrayList<>();
    }
    
    /**
     * Get price offer by id
     * @param id price offer id
     * @return Price offer, else null
     */
    @Operation(summary = "Get price offer by id",
            method = "GET",
            parameters = {
                    @Parameter(name = "id", description = "ID to price offer to get", required = true)
            })
    @ApiResponse(responseCode = "200", description = "Price offer object if found, else none.")
    @GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferDTO getById(@PathVariable("id") Long id) {
        if(id != null) {
            Optional<PriceOffer> optPriceOffer = service.findById(id);
            if(optPriceOffer.isEmpty()) {
                log.info("Could not find a price offer with id: " + id);
                return null;
            }
            PriceOffer priceOffer = optPriceOffer.get();
            
            log.debug("Returning priceOffer: {}", priceOffer);
            return modelMapper.map(priceOffer, PriceOfferDTO.class);
        }
        
        return null;
    }
    
    @ExceptionHandler({EmployeeNotProvidedException.class})
    public ResponseEntity<Object> handleException() {
        return new ResponseEntity<>("Sales employee not set", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Create new Price offer
     * @param priceOfferDTO Price offer values
     * @return Newly created price offer
     * @throws JsonProcessingException if not real JSON is passed
     */
    @Operation(summary = "Create a new price offer",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "PriceOfferDTO object with all values to persist.")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Newly created price offer")
    })
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceOfferDTO> create(@RequestBody PriceOfferDTO priceOfferDTO) throws JsonProcessingException {
        log.debug("Got new Price offer object: " + priceOfferDTO);
        
        if(priceOfferDTO.getSalesEmployee() == null) throw new EmployeeNotProvidedException();
        if(priceOfferDTO.getCustomerName() == null) throw new CustomerNotProvidedException("Customer name not provided.");

        PriceOffer priceOffer = modelMapper.map(priceOfferDTO, PriceOffer.class);

        priceOffer.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
        StopWatch watch = new StopWatch();
        watch.start();
        priceOffer = service.save(priceOffer);
        watch.stop();
        log.debug("Time used to create price offer: {} ms", watch.getTime());
        
        return ResponseEntity.ok(modelMapper.map(priceOffer, PriceOfferDTO.class));
    }

    /**
     * Update PriceOffer with new status
     * @param id price offer id
     * @param status the new status to set
     * @return Message if the update was successfull.
     */
    @Operation(summary = "Set new status for the price offer by id.",
            method = "PUT",
            parameters = {
                    @Parameter(name = "id", required = true, description = "ID for price offer to update"),
                    @Parameter(name = "status", required = true, description = "The status to update the price offer with.")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns status OK when status has successfully been updated."),
            @ApiResponse(responseCode = "400", description = "Price offer not found"),
            @ApiResponse(responseCode = "404", description = "Given status not found")
    })
    @PutMapping("/status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        if(!PriceOfferStatus.getAllPriceOfferStatuses().contains(status)) {
            String message = String.format("Given status is not valid: %s", status);
            throw new PriceOfferStatusCodeNotFoundException(message);
        }

        service.updateStatus(id, status);

        String returnMessage = String.format("Price offer with id: %d was updated with status: %s", id, status);
        return new ResponseEntity<>(returnMessage, HttpStatus.OK);
    }

    @ExceptionHandler({PriceOfferStatusCodeNotFoundException.class})
    public ResponseEntity<Object> handlePriceOfferStatusCodeNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    /**
     * Update price offer
     * @param id Price offer id
     * @param priceOfferDTO New values for price offer
     * @return Updated price offer
     * @throws JsonProcessingException if not real JSON is passed.
     */
    @Operation(summary = "Update price offer",
            method = "PUT")
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferDTO save(@PathVariable("id") Long id, @RequestBody PriceOfferDTO priceOfferDTO) throws JsonProcessingException {
        log.debug("Trying to update price offer with id: {}", id);
        log.debug("Values received for PriceOffer: {}", priceOfferDTO);
        
        Optional<PriceOffer> result = service.findById(id);
        
        if(result.isEmpty()) {
            log.debug("{} {} {}", "Price offer with ID", id, "was not found");
            return null;
        }
        
        PriceOffer updatedOffer = modelMapper.map(priceOfferDTO, PriceOffer.class);

        StopWatch watch = new StopWatch();
        watch.start();
        updatedOffer = service.save(updatedOffer);
        watch.stop();
        log.debug("Time used to update price offer: {} ms", watch.getTime());

        return modelMapper.map(updatedOffer, PriceOfferDTO.class);
    }

    /**
     * Soft deletes price offer by id
     * @param id Price offer id
     * @return true if set to deleted, else false
     */
    @Operation(summary = "Soft delete price offer by id",
            method = "DELETE",
            parameters = {
                @Parameter(name = "id", description = "ID for Price offer to be soft deleted", required = true)
            }
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "True if price offer was set to deleted, else false")})
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Deleting PriceOffer with id: {}", id);
        return service.delete(id);
    }

    /**
     * Force deletes price offer by id
     * @param id Price offer id
     * @return true if deleted, else false
     */
    @Operation(summary = "Force delete price offer by id",
            method = "DELETE",
            parameters = {
                    @Parameter(name = "id", description = "ID for Price offer to be deleted", required = true)
            }
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "True if price offer was deleted, else false")})
    @DeleteMapping(path = "/force/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean forceDelete(@PathVariable("id") Long id) {
        log.debug("Force deleting PriceOffer with id: {}", id);
        return service.forceDeleteById(id);
    }

    /**
     * Get all price offers ready for BO-report.
     * @return List of all price offers ready for BO-report.
     */
    @Operation(summary = "Get all price offers ready for BO-report (aka. Price report)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of PriceOfferListDTO objects.")
    })
    @GetMapping(path = "/bo-report/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferListDTO> getPriceOffersReadyForBoReport() {
        log.debug("Getting all offers ready for BO-report");

        List<PriceOffer> priceOffersForBoReport = service.findAllPriceOffersRadyForBoReport();

        log.debug("Amount of offers for BO-report: {}", priceOffersForBoReport.size());
        return priceOffersForBoReport.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferListDTO.class)).toList();
    }
}
