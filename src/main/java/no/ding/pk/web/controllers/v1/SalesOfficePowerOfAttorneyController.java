package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.web.dto.web.client.SalesOfficePowerOfAttorneyDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/sales-office-power-of-attorney")
public class SalesOfficePowerOfAttorneyController {

    private final static Logger log = LoggerFactory.getLogger(SalesOfficePowerOfAttorneyController.class);

    private final SalesOfficePowerOfAttorneyService service;
    private final ModelMapper modelMapper;

    @Autowired
    public SalesOfficePowerOfAttorneyController(SalesOfficePowerOfAttorneyService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesOfficePowerOfAttorneyDTO byId(@PathVariable("id") Long id) {
        log.debug("Getting entity for id {}", id);

        PowerOfAttorney poa = service.findById(id);

        if(poa != null) {
            return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
        }

        return null;
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOfficePowerOfAttorneyDTO> list() {
        log.debug("Received request for all PowerOfAttorneys");
        List<PowerOfAttorney> list = service.findAll();

        return list.stream().map(poa -> modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class)).collect(Collectors.toList());
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesOfficePowerOfAttorneyDTO create(@RequestBody SalesOfficePowerOfAttorneyDTO sopa) {
        log.debug("Creating new Power of Attorney");

        PowerOfAttorney poa = modelMapper.map(sopa, PowerOfAttorney.class);

        poa = service.save(poa);

        return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
    }

    @PutMapping(path = "/save/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesOfficePowerOfAttorneyDTO save(@PathVariable("id") Long id, @RequestBody SalesOfficePowerOfAttorneyDTO sopoaDTO) {
        log.debug("Trying to update poa with id {}", id);

        PowerOfAttorney poa = modelMapper.map(sopoaDTO, PowerOfAttorney.class);

        poa = service.save(poa);

        return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
    }

    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Trying to delete entity with id {}", id);

        return service.delete(id);
    }

}
