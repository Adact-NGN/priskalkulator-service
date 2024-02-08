package no.ding.pk.web.controllers.v1.bo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.KeyCombination;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.v1.bo.BoKeyCodeSuggestionDTO;
import no.ding.pk.web.dto.v1.bo.ConditionCodeDTO;
import no.ding.pk.web.dto.v1.bo.KeyCombinationDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "BoReportConditionController", description = "Controller for handling Back Office reports.")
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
    @Operation(summary = "BoReport - Get list of ConditionCodeDTO",
            method = "GET",
            parameters = {
                    @Parameter(name = "code",
                            description = "Code type to get ConditionCodes for."
                    )
            },
            tags = "BoReportConditionController"
    )
    @GetMapping(path = "/list")
    public List<ConditionCodeDTO> list(@RequestParam(value = "code", required = false) String code) {
        log.debug("Requesting for all title types with key combinations.");
        List<ConditionCode> conditionCodes = service.getAllConditionCodes(code);

        return conditionCodes.stream().map(titleType -> modelMapper.map(titleType, ConditionCodeDTO.class)).toList();
    }

    /**
     * Get list of KeyCombination based on selected ConditionCode
     * @param conditionCode condition code to look up key combinations for, not required.
     * @return List of all key combinations, else if condition code is given all key combinations related to given code.
     */
    @Operation(summary = "BoReport - Get list of KeyCombination based on selected ConditionCode.",
            method = "GET",
            parameters = {
                    @Parameter(name = "conditionCode",
                            description = "The condition code to get key combinations for."
                    )
            },
            tags = "BoReportConditionController"
    )
    @GetMapping(path = "/list/key-combination")
    public List<KeyCombinationDTO> listKeyCombinations(@RequestParam(name = "conditionCode", required = false) String conditionCode) {
        if(StringUtils.isNotBlank(conditionCode)) {
            List<KeyCombination> keyCombinations = service.getKeyCombinationByConditionCode(conditionCode);

            return keyCombinations.stream().map(keyCombination -> modelMapper.map(keyCombination, KeyCombinationDTO.class)).toList();
        }

        List<KeyCombination> keyCombinations = service.getKeyCombinationList();

        return keyCombinations.stream().map(keyCombination -> modelMapper.map(keyCombination, KeyCombinationDTO.class)).toList();
    }

    /**
     * Get condition code and key combination suggestion map for all materials in a price offer.
     * @param priceOfferId price offer id
     * @return Map of condition codes and key combinations
     */
    @Operation(summary = "Get condition code and key combination suggestion map for all materials in a price offer.",
            method = "GET",
            parameters = {
                    @Parameter(name = "id",
                            description = "Price offer id.", required = true
                    )
            },
            tags = "BoReportConditionController"
    )
    @GetMapping(path = "/suggestions/price-offer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, BoKeyCodeSuggestionDTO>> getSuggestions(@PathVariable("id") Long priceOfferId) {
        Optional<PriceOffer> priceOfferOptional = priceOfferService.findById(priceOfferId);

        if(priceOfferOptional.isEmpty()) {
            log.debug("No price offer found with id: {}", priceOfferId);
            return new HashMap<>();
        }

        log.debug("Found price offer with id, {}, finding suggestions.", priceOfferId);

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
