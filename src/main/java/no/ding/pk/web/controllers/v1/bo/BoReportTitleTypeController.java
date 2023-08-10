package no.ding.pk.web.controllers.v1.bo;

import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.service.bo.BoReportTitleTypeService;
import no.ding.pk.web.dto.v1.bo.TitleTypeDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/bo-report/title-type")
public class BoReportTitleTypeController {
    private static final Logger log = LoggerFactory.getLogger(BoReportTitleTypeController.class);

    private final BoReportTitleTypeService service;

    private final ModelMapper modelMapper;

    @Autowired
    public BoReportTitleTypeController(BoReportTitleTypeService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    /**
     * Get list of {@code TitleTypeDTO}
     * @return List of {@code TitleTypeDTO}
     */
    @GetMapping(path = "/list")
    public List<TitleTypeDTO> list(@RequestParam(value = "type", required = false) String type) {
        log.debug("Requesting for all title types with key combinations.");
        List<ConditionCode> conditionCodes = service.getAllTitleTypes(type);

        return conditionCodes.stream().map(titleType -> modelMapper.map(titleType, TitleTypeDTO.class)).toList();
    }
}
