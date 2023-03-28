package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.web.dto.web.client.SalesOfficePowerOfAttorneyDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-office-power-of-attorney")
public class SalesOfficePowerOfAttorneyController {

    private final SalesOfficePowerOfAttorneyService service;
    private final ModelMapper modelMapper;

    @Autowired
    public SalesOfficePowerOfAttorneyController(SalesOfficePowerOfAttorneyService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_PDF_VALUE)
    public SalesOfficePowerOfAttorneyDTO list() {
        List<PowerOfAttorney> list = service.findAll();

        return modelMapper.map(list, SalesOfficePowerOfAttorneyDTO.class);
    }
}
