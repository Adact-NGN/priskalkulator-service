package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferListDTO;
import no.ding.pk.web.dto.web.client.offer.PriceRowDTO;
import no.ding.pk.web.dto.web.client.requests.ActivatePriceOfferRequest;
import no.ding.pk.web.dto.web.client.requests.ApprovalRequest;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.handlers.CustomerNotProvidedException;
import no.ding.pk.web.handlers.EmployeeNotProvidedException;
import no.ding.pk.web.handlers.MissingTermsInRequestPayloadException;
import no.ding.pk.web.handlers.PriceOfferStatusCodeNotFoundException;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestController(value = "priceOfferControllerV2")
@RequestMapping("/api/v2/price-offer")
public class PriceOfferController {
    private static final Logger log = LoggerFactory.getLogger(PriceOfferController.class);
    
    private final PriceOfferService service;
    
    private final SalesOfficePowerOfAttorneyService sopoaService;
    
    private final ModelMapper modelMapper;
    
    @Autowired
    public PriceOfferController(
            PriceOfferService service,
            SalesOfficePowerOfAttorneyService sopoaService,
            @Qualifier(value = "modelMapperV2") ModelMapper modelMapper) {
        this.service = service;
        this.sopoaService = sopoaService;
        this.modelMapper = modelMapper;
    }
    
    /**
     * Get all price offers
     * @param statuses list of all price offer with statuses to be inn the list.
     * @return List of price offers, else empty list
     */
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

    @GetMapping(path = "/list/{salesOffice}")
    public ResponseEntity<List<PriceOfferListDTO>> listAllBySalesOffice(@PathVariable("salesOffice") String salesOffice, @RequestParam(value = "statuses", required = false) String statuses) {

        List<PriceOffer> priceOffers;

        if(StringUtils.isNotBlank(statuses)) {
            List<String> statusList = Arrays.stream(statuses.split(",")).toList();
            priceOffers = service.findAllBySalesOfficeAndStatus(List.of(salesOffice), statusList);
        } else {
            priceOffers = service.findAllBySalesOfficeAndStatus(List.of(salesOffice), null);
        }

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
     * Adds missing fields for the {@code Material} object. The ModelMapper is unable to map all fields when converting from a string to an object.
     * @param priceOfferDTO incoming price {@code PriceOfferDTO} offer to get missing values from
     * @param priceOffer converted {@code PriceOffer} to update
     */
    private void mapMaterialValues(PriceOfferDTO priceOfferDTO, PriceOffer priceOffer) {
        if(priceOfferDTO.getSalesOfficeList() == null) {
            return;
        }
        priceOfferDTO.getSalesOfficeList().forEach(salesOfficeDTO -> {
            SalesOffice salesOffice = priceOffer.getSalesOfficeList().stream().filter(so -> salesOfficeDTO.getSalesOffice().equals(so.getSalesOffice())).findAny().orElse(null);

            if(salesOfficeDTO.getMaterialList() != null) {
                salesOfficeDTO.getMaterialList().forEach(updateMaterialInPriceRowsIn(salesOffice));
            }

            if(salesOfficeDTO.getRentalList() != null) {
                salesOfficeDTO.getRentalList().forEach(updateMaterialInPriceRowsIn(salesOffice));
            }

            if(salesOfficeDTO.getTransportServiceList() != null) {
                salesOfficeDTO.getTransportServiceList().forEach(updateMaterialInPriceRowsIn(salesOffice));
            }
        });
    }

    private Consumer<PriceRowDTO> updateMaterialInPriceRowsIn(SalesOffice salesOffice) {
        return priceRowDTO -> {
            String materialNumber = priceRowDTO.getMaterial();

            PriceRow priceRow = salesOffice.getMaterialList().stream().filter(pr -> materialNumber.equals(pr.getMaterial().getMaterialNumber())).findAny().orElse(null);

            if (priceRow == null) {
                return;
            }
            Material material = priceRow.getMaterial();

            if (material != null) {
                createMaterialFromPriceRowDTO(material, priceRowDTO, salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), priceRowDTO.getSalesZone());
            }
        };
    }

    private void createMaterialFromPriceRowDTO(Material to, PriceRowDTO from, String salesOrg, String salesOffice, String salesZone) {
        log.debug("To: {}, from: {}", to, from);
        to.setDesignation(from.getDesignation());
        to.setMaterialGroupDesignation(from.getProductGroupDesignation());
        to.setMaterialTypeDescription(from.getMaterialDesignation());
        to.setDeviceType(from.getDeviceType());
        MaterialPrice materialStdPrice = MaterialPrice
                .builder(salesOrg, salesOffice, from.getMaterial(), from.getDeviceType(), salesZone)
                .standardPrice(from.getStandardPrice())
                .pricingUnit(from.getPricingUnit())
                .quantumUnit(from.getQuantumUnit())
                .build();
        to.setMaterialStandardPrice(materialStdPrice);

        to.setPricingUnit(from.getPricingUnit());
        to.setQuantumUnit(from.getQuantumUnit());

        to.setCategoryId(from.getCategoryId());
        to.setCategoryDescription(from.getCategoryDescription());
        to.setSubCategoryId(from.getSubCategoryId());
        to.setSubCategoryDescription(from.getSubCategoryDescription());
        to.setClassId(from.getClassId());
        to.setClassDescription(from.getClassDescription());
    }

    /**
     * Update price offer
     * @param id Price offer id
     * @param priceOfferDTO New values for price offer
     * @return Updated price offer
     * @throws JsonProcessingException if not real JSON is passed.
     */
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

        if(StringUtils.isNotBlank(updatedOffer.getMaterialsForApproval())) {
            updatedOffer.setNeedsApproval(true);
            updatedOffer.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
        }

        // TODO: This probably overwrites what the service layer is doing :S
        if(priceOfferCandidateForApprovalAndIsNotApproved(updatedOffer)) {
            List<Integer> salesOffices = collectSalesOfficeNumbers(updatedOffer);
            
            if(!salesOffices.isEmpty()) {
                List<PowerOfAttorney> poa = sopoaService.findBySalesOfficeInList(salesOffices);
                
                if(!poa.isEmpty()) {
                    User approver = getApproverForPriceOffer(poa);
                    
                    if(approver != null) {
                        updatedOffer.setApprover(approver);

                        updatedOffer = service.save(updatedOffer);
                    } else {
                        log.debug("Could not find any eligible approver for offer with id: {}, sales offices: {}", updatedOffer.getId(), salesOffices);
                    }
                } else {
                    log.debug("No Power of attorneys found for any sales offices added to the price offer: {}", salesOffices);
                }
            }
        }
        
        return modelMapper.map(updatedOffer, PriceOfferDTO.class);
    }

    private static List<Integer> collectSalesOfficeNumbers(PriceOffer updatedOffer) {
        return updatedOffer.getSalesOfficeList().stream().map(salesOffice -> Integer.parseInt(salesOffice.getSalesOffice())).collect(Collectors.toList());
    }

    private static boolean priceOfferCandidateForApprovalAndIsNotApproved(PriceOffer updatedOffer) {
        return updatedOffer.getNeedsApproval() && !PriceOfferStatus.isApprovalState(updatedOffer.getPriceOfferStatus());
    }

    private User getApproverForPriceOffer(List<PowerOfAttorney> poa) {
        Optional<PowerOfAttorney> findAnyPoaLvl1 = poa.stream().filter(tmppoa -> tmppoa.getOrdinaryWasteLvlOneHolder() != null).findAny();
        
        if(findAnyPoaLvl1.isPresent()) {
            PowerOfAttorney lvl1Poa = findAnyPoaLvl1.get();
            return lvl1Poa.getOrdinaryWasteLvlOneHolder();
        } else {
            Optional<PowerOfAttorney> findAnyPoaLvl2 = poa.stream().filter(tmppoa -> tmppoa.getOrdinaryWasteLvlTwoHolder() != null).findAny();
            
            if(findAnyPoaLvl2.isPresent()) {
                return findAnyPoaLvl2.get().getOrdinaryWasteLvlTwoHolder();
            }
        }
        return null;
    }
    
    /**
     * Soft deletes price offer by id
     * @param id Price offer id
     * @return true if deleted, else false
     */
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Deleting PriceOffer with id: {}", id);
        return service.delete(id);
    }

    /**
     * Get all price offers ready for BO-report.
     * @return List of all price offers ready for BO-report.
     */
    @GetMapping(path = "/bo-report/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferListDTO> getPriceOffersReadyForBoReport() {
        log.debug("Getting all offers ready for BO-report");

        List<PriceOffer> priceOffersForBoReport = service.findAllPriceOffersRadyForBoReport();

        log.debug("Amount of offers for BO-report: {}", priceOffersForBoReport.size());
        return priceOffersForBoReport.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferListDTO.class)).toList();
    }
}
