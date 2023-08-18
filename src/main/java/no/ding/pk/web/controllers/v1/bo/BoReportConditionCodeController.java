package no.ding.pk.web.controllers.v1.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.v1.bo.BoKeyCodeSuggestionDTO;
import no.ding.pk.web.dto.v1.bo.ConditionCodeDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/v1/bo-report/condition-code")
public class BoReportConditionCodeController {
    private static final Logger log = LoggerFactory.getLogger(BoReportConditionCodeController.class);

    private final BoReportConditionCodeService service;
    private final PriceOfferService priceOfferService;

    private final ModelMapper modelMapper;

    @Autowired
    public BoReportConditionCodeController(BoReportConditionCodeService service, PriceOfferService priceOfferService, ModelMapper modelMapper) {
        this.service = service;
        this.priceOfferService = priceOfferService;
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

    /**
     * Get condition code and key combination suggestion map for all materials in a price offer.
     * @param priceOfferId price offer id
     * @return Map of condition codes and key combinations
     */
    @GetMapping(path = "/suggestions/price-offer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, BoKeyCodeSuggestionDTO>> getSuggestions(@PathVariable("id") Long priceOfferId) {
        Optional<PriceOffer> priceOfferOptional = priceOfferService.findById(priceOfferId);

        if(priceOfferOptional.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Map<String, BoReportCondition>> priceOfferBoConditionalMap = service.buildBoReportConditionMapForPriceOffer(priceOfferOptional.get());

        Map<String, Map<String, SuggestedConditionCodeKeyCombination>> suggestionMap = service.getSuggerstionsForPriceOfferBoConditionalMap(priceOfferBoConditionalMap);

        return suggestionMapToDTO(suggestionMap);
    }

    private Map<String, Map<String, BoKeyCodeSuggestionDTO>> suggestionMapToDTO(Map<String, Map<String, SuggestedConditionCodeKeyCombination>> suggestionMap) {
        Map<String, Map<String, BoKeyCodeSuggestionDTO>> suggestionMapDTO = new HashMap<>();

        for (String salesOffice : suggestionMap.keySet()) {
            Map<String, SuggestedConditionCodeKeyCombination> conditionMap = suggestionMap.get(salesOffice);
            Map<String, BoKeyCodeSuggestionDTO> materialSuggestionDTOMap = new HashMap<>();

            for (String materialNumber : conditionMap.keySet()) {
                SuggestedConditionCodeKeyCombination suggestion = conditionMap.get(materialNumber);

                BoKeyCodeSuggestionDTO suggestedDTO = modelMapper.map(suggestion, BoKeyCodeSuggestionDTO.class);

                materialSuggestionDTOMap.put(materialNumber, suggestedDTO);
            }

            suggestionMapDTO.put(salesOffice, materialSuggestionDTOMap);
        }

        return suggestionMapDTO;
    }
}
