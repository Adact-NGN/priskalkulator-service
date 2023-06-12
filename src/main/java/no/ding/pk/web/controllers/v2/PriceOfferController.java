package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTerms;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.dto.web.client.offer.TermsDTO;
import no.ding.pk.web.dto.web.client.requests.ApprovalRequest;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.handlers.CustomerNotProvidedException;
import no.ding.pk.web.handlers.EmployeeNotProvidedException;
import org.apache.commons.lang3.StringUtils;
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
    public List<PriceOfferDTO> list(@RequestParam(value = "statuses", required = false) String statuses) {

        List<PriceOffer> priceOfferList;
        if(StringUtils.isNotBlank(statuses)) {
            List<String> statusList = new ArrayList<>(Arrays.stream(statuses.split(",")).toList());
            priceOfferList = service.findAllByPriceOfferStatusInList(statusList);
        } else {
            priceOfferList = service.findAllWithoutStatusInList(List.of(PriceOfferStatus.ACTIVATED.getStatus()));
        }

        if(!priceOfferList.isEmpty()) {
            return priceOfferList.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferDTO.class)).collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Get all price offers created by sales employee
     * @param salesEmployeeId User id to list price offers for.
     * @return list of price offers connected to sales employee, else empty list.
     */
    @GetMapping(path = "/list/{salesEmployeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferDTO> listBySalesEmployee(@PathVariable("salesEmployeeId") Long salesEmployeeId) {
        List<PriceOffer> priceOffers = service.findAllBySalesEmployeeId(salesEmployeeId);
        
        if(!priceOffers.isEmpty()) {
            return priceOffers.stream().map(priceOffer -> modelMapper.map(priceOffers, PriceOfferDTO.class)).collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }

    /**
     * Activate price offer
     * @param approverId id for {@code User}
     * @param priceOfferId id for price offer
     * @param customerTermsDTO completed customer terms to be added to the price offer.
     * @return true if price offer is updated, else false
     */
    @PutMapping(path = "/activate/{approverId}/{priceOfferId}")
    public Boolean activatePriceOffer(@PathVariable("approverId") Long approverId,
                                      @PathVariable("priceOfferId") Long priceOfferId,
                                      @RequestBody TermsDTO customerTermsDTO) {
        log.debug("Activating price offer with id {} by user with id {}", priceOfferId, approverId);

        PriceOfferTerms customerTerms = modelMapper.map(customerTermsDTO, PriceOfferTerms.class);
        return service.activatePriceOffer(approverId, priceOfferId, customerTerms);
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
        return service.approvePriceOffer(priceOfferId, approverId, approvalRequest.getStatus(), approvalRequest.getDismissalReason());
    }
    
    /**
     * Find and filter all price offers for approver.
     * @param approverId approver user id
     * @param priceOfferStatus price offer status to filter on {@code not required}
     * @return List of price offers for approver
     */
    @GetMapping(path = "/list/approver/{approverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferDTO> listByApprover(@PathVariable("approverId") Long approverId,
                                              @RequestParam(value = "status", required = false) String priceOfferStatus) {
        List<PriceOffer> priceOffers = service.findAllByApproverIdAndPriceOfferStatus(approverId, priceOfferStatus);
        
        if(!priceOffers.isEmpty()) {
            log.debug("Found price offers, {}, for approver", priceOffers.size());
            PriceOfferDTO[] priceOfferDTOS = modelMapper.map(priceOffers, PriceOfferDTO[].class);
            log.debug("Mapped {} amount", priceOfferDTOS.length);
            return Arrays.stream(priceOfferDTOS).toList();
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
        if(priceOfferDTO.getCustomerNumber() == null) throw new CustomerNotProvidedException();
        
        PriceOffer priceOffer = modelMapper.map(priceOfferDTO, PriceOffer.class);
        
        priceOffer.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
        priceOffer = service.save(priceOffer);
        
        return ResponseEntity.ok(modelMapper.map(priceOffer, PriceOfferDTO.class));
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
        
        updatedOffer = service.save(updatedOffer);

        if(StringUtils.isNotBlank(updatedOffer.getMaterialsForApproval())) {
            updatedOffer.setNeedsApproval(true);
            updatedOffer.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
        }

        if(priceOfferCandidateForApprovalAndIsNotApproved(updatedOffer)) {
            List<Integer> salesOffices = updatedOffer.getSalesOfficeList().stream().map(salesOffice -> Integer.parseInt(salesOffice.getSalesOffice())).collect(Collectors.toList());
            
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
    public List<PriceOffer> getPriceOffersReadyForBoReport() {
        log.debug("Getting all offers ready for BO-report");

        List<PriceOffer> priceOffersForBoReport = service.findAllPriceOffersRadyForBoReport();

        log.debug("Ammount of offers for BO-report: {}", priceOffersForBoReport.size());
        return priceOffersForBoReport;
    }
}
