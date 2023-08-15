package no.ding.pk.web.controllers.v1.bo;

import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.web.dto.v1.bo.BoKeyCodeSuggestionDTO;
import no.ding.pk.web.dto.v1.bo.ConditionCodeDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/bo-report/condition-code")
public class BoReportConditionCodeController {
    private static final Logger log = LoggerFactory.getLogger(BoReportConditionCodeController.class);

    private final BoReportConditionCodeService service;

    private final ModelMapper modelMapper;

    @Autowired
    public BoReportConditionCodeController(BoReportConditionCodeService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    /**
     * Get list of {@code ConditionCodeDTO}
     * @return List of {@code ConditionCodeDTO}
     */
    @GetMapping(path = "/list")
    public List<ConditionCodeDTO> list(@RequestParam(value = "type", required = false) String type) {
        log.debug("Requesting for all title types with key combinations.");
        List<ConditionCode> conditionCodes = service.getAllConditionCodes(type);

        return conditionCodes.stream().map(titleType -> modelMapper.map(titleType, ConditionCodeDTO.class)).toList();
    }

    @GetMapping(path = "/suggestions")
    public List<BoKeyCodeSuggestionDTO> getSuggestions(@RequestBody PriceOfferDTO priceOffer) {
        // TODO: Collect all materials, get all parameters to set BoReportCondition for all materials
        return new ArrayList<>();
    }
}
