package no.ding.pk.web.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.v1.web.client.PriceOfferDTO;
import org.apache.commons.lang3.time.StopWatch;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for getting Price offers.
 * @deprecated This controller will be removed in future releases. Use /api/v2/price-offer
 */
@Tag(name = "PriceOfferControllerV1", description = "Controller for handling price offers.")
@RestController
@RequestMapping("/api/v1/price-offer")
public class PriceOfferController {
    
    private static final Logger log = LoggerFactory.getLogger(PriceOfferController.class);

    private final PriceOfferService service;

    private final ModelMapper modelMapper;

    @Autowired
    public PriceOfferController(ObjectMapper objectMapper, PriceOfferService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    /**
     * List all {@code PriceOffer}
     * @return List of {@code PriceOffer}
     */
    @Operation(description = "List all Price offers",
            method = "GET",
            tags = "PriceOfferControllerV1"
    )
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_Sales')")
    public List<PriceOffer> list() {
        List<PriceOffer> priceOfferList = service.findAll();
        
        if(!priceOfferList.isEmpty()) {
            return priceOfferList;
        }
        
        return new ArrayList<>();
    }

    /**
     * Get {@code PriceOffer} by id
     * @param id for entity to get.
     * @return PriceOffer object, else empty if not found
     */
    @Operation(description = "Get Price offer by ID",
            method = "GET",
            parameters = {
                    @Parameter(name = "id", description = "ID for PriceOffer to get", required = true)
            },
            tags = "PriceOfferControllerV1"
    )
    @GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer getById(@PathVariable("id") Long id) {
        if(id != null) {
            Optional<PriceOffer> optPriceOffer = service.findById(id);
            if(optPriceOffer.isEmpty()) {
                log.info("Could not find a price offer with id: " + id);
                return null;
            }
            PriceOffer priceOffer = optPriceOffer.get();

            log.debug("Returning priceOffer: {}", priceOffer);
            return priceOffer;
        }
        
        return null;
    }

    /**
     * Create a new {@code PriceOffer}
     * @param priceOfferDTO The {@code PriceOffer} to create
     * @return Newly created {@code PriceOffer}
     */
    @Operation(description = "Create a new PriceOffer",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "PriceOfferDTO"),
            tags = "PriceOfferControllerV1"
    )
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer create(@RequestBody PriceOfferDTO priceOfferDTO) {
        log.debug("Got new Price offer object: " + priceOfferDTO);
        
        PriceOffer priceOffer = mapToEntity(priceOfferDTO);


        StopWatch watch = new StopWatch();
        watch.start();
        PriceOffer offer = service.save(priceOffer);
        watch.stop();
        log.debug("Time used to create price offer: {} ms", watch.getTime());
        log.debug("PriceOffer created, returning...");
        return offer;
    }

    private PriceOffer mapToEntity(PriceOfferDTO priceOfferDto) {
        return modelMapper.map(priceOfferDto, PriceOffer.class);
    }

    /**
     * Save updated {@code PriceOffer}
     * @param id {@code PriceOffer} id
     * @param priceOfferDTO updated {@code PriceOffer} object
     * @return Updated {@code PriceOffer}
     */
    @Operation(description = "Create a new PriceOffer",
            method = "PUT",
            parameters = {
                    @Parameter(name = "id", description = "ID for PriceOffer to update.", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "PriceOfferDTO"),
            tags = "PriceOfferControllerV1"
    )
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer save(@PathVariable("id") Long id, @RequestBody PriceOfferDTO priceOfferDTO) {
        log.debug("Trying to update price offer with id: " + id);
        log.debug("Values received for PriceOffer: {}", priceOfferDTO);
        
        if(id == null) {
            log.error("Put request was given non existing price offer to update.");
            return null;
        }
        
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
        
        return updatedOffer;
    }

    /**
     * Delete {@code PriceOffer} by id
     * @param id The id for the {@code PriceOffer} to delete
     * @return {@code true} if successful, else {@code false}
     */
    @Operation(description = "Delete PriceOffer by ID",
            method = "DELETE",
            parameters = {
                    @Parameter(name = "id", description = "ID for PriceOffer to delete", required = true)
            },
            tags = "PriceOfferControllerV1"
    )
    @DeleteMapping(path = "/delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Deleting PriceOffer with id: {}", id);
        return service.delete(id);
    }

}
