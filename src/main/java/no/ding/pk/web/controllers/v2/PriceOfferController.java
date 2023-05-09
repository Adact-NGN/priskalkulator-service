package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.handlers.CustomerNotProvidedException;
import no.ding.pk.web.handlers.EmployeeNotProvidedException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import java.util.stream.Collectors;

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

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferDTO> list() {
        List<PriceOffer> priceOfferList = service.findAll();

        if(!priceOfferList.isEmpty()) {
            return priceOfferList.stream().map(priceOffer -> modelMapper.map(priceOffer, PriceOfferDTO.class)).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

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

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceOfferDTO> create(@RequestBody PriceOfferDTO priceOfferDTO) throws JsonProcessingException {
        log.debug("Got new Price offer object: " + priceOfferDTO);

        if(priceOfferDTO.getSalesEmployee() == null) throw new EmployeeNotProvidedException();
        if(priceOfferDTO.getCustomerNumber() == null) throw new CustomerNotProvidedException();

        PriceOffer priceOffer = modelMapper.map(priceOfferDTO, PriceOffer.class);

        log.debug("Resulting priceOffer: {}", priceOffer.toString());

        priceOffer.setPriceOfferStatus(PriceOfferStatus.OFFER_CREATED.getStatus());
        priceOffer = service.save(priceOffer);

        return ResponseEntity.ok(modelMapper.map(priceOffer, PriceOfferDTO.class));
    }

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

        return modelMapper.map(updatedOffer, PriceOfferDTO.class);
    }

    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Deleting PriceOffer with id: {}", id);
        return service.delete(id);
    }
}
