package no.ding.pk.web.controllers.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.web.client.PriceOfferDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for getting Price offers.
 * @deprecated This controller will be removed in future releases. Use /api/v2/price-offer
 */
@RestController
@RequestMapping("/api/v1/price-offer")
public class PriceOfferController {
    
    private static final Logger log = LoggerFactory.getLogger(PriceOfferController.class);
    
    private final ObjectMapper objectMapper;
    
    private final PriceOfferService service;

    private final ModelMapper modelMapper;

    @Autowired
    public PriceOfferController(ObjectMapper objectMapper, PriceOfferService service, ModelMapper modelMapper) {
        this.objectMapper = objectMapper;
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOffer> list() {
        List<PriceOffer> priceOfferList = service.findAll();
        
        if(!priceOfferList.isEmpty()) {
            return priceOfferList;
        }
        
        return new ArrayList<>();
    }
    
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

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer create(@RequestBody PriceOfferDTO priceOfferDTO) throws JsonProcessingException {
        log.debug("Got new Price offer object: " + priceOfferDTO);
        
        PriceOffer priceOffer = convertToEntity(priceOfferDTO);
        
        log.debug("Resulting priceOffer");
        log.debug(priceOffer.toString());
        return service.save(priceOffer);
    }

    private PriceOffer convertToEntity(PriceOfferDTO priceOfferDto) {
        return modelMapper.map(priceOfferDto, PriceOffer.class);
    }

    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer save(@PathVariable("id") Long id, @RequestBody String plainPriceOfferDTO) throws JsonProcessingException {
        log.debug("Trying to update price offer with id: " + id);
        log.debug("Values received for PriceOffer: {}", plainPriceOfferDTO);
        
        if(id == null) {
            log.error("Put request was given non existing price offer to update.");
            return null;
        }
        
        Optional<PriceOffer> result = service.findById(id);
        
        if(result.isEmpty()) {
            log.debug("{} {} {}", "Price offer with ID", id, "was not found");
            return null;
        }

        PriceOffer updatedOffer = objectMapper.readValue(plainPriceOfferDTO, PriceOffer.class);

        updatedOffer = service.save(updatedOffer);
        
        return updatedOffer;
    }

    @DeleteMapping(path = "/delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Deleting PriceOffer with id: {}", id);
        return service.delete(id);
    }

}
