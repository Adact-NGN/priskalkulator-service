package no.ding.pk.web.controllers.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.ding.pk.web.dto.web.client.PriceOfferDTO;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.offer.PriceOfferService;

@RestController
@RequestMapping("/api/v1/price-offer")
public class PriceOfferController {
    
    private static final Logger log = LoggerFactory.getLogger(PriceOfferController.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PriceOfferService service;
    
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
            if(!optPriceOffer.isPresent()) {
                log.info("Could not find a price offer with id: " + id);
                return null;
            }
            
            return optPriceOffer.get();
        }
        
        return null;
    }
    
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer create(@RequestBody String newPriceOffer) throws JsonMappingException, JsonProcessingException {
        log.debug("Got new Price offer object: " + newPriceOffer);
        
        PriceOffer priceOffer = objectMapper.readValue(newPriceOffer, PriceOffer.class);
        
        log.debug("Resulting priceOffer");
        log.debug(priceOffer.toString());
        return service.save(priceOffer);
    }
    
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOffer save(@PathVariable("id") Long id, @RequestBody String plainPriceOfferDTO) throws JsonMappingException, JsonProcessingException {
        log.debug("Trying to update price offer with id: " + id);
        log.debug("Values received for PriceOffer: {}", plainPriceOfferDTO);
        
        if(id == null) {
            log.error("Put request was given non existing price offer to update.");
            return null;
        }
        
        Optional<PriceOffer> result = service.findById(id);
        
        if(!result.isPresent()) {
            log.debug("{} {}", "Price offer with ID", id, "was not found");
            return null;
        }

        PriceOffer updatedOffer = objectMapper.readValue(plainPriceOfferDTO, PriceOffer.class);

        updatedOffer = service.save(updatedOffer);
        
        return updatedOffer;
    }

}
