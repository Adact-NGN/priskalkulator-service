package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.domain.offer.template.TemplateMaterial;
import no.ding.pk.service.UserService;
import no.ding.pk.service.offer.PriceOfferTemplateService;
import no.ding.pk.web.dto.web.client.offer.template.PriceOfferTemplateDTO;
import no.ding.pk.web.handlers.PriceOfferTemplateNotFound;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/price-offer-template")
public class PriceOfferTemplateController {

    private static final Logger log = LoggerFactory.getLogger(PriceOfferTemplateController.class);
    
    private final PriceOfferTemplateService service;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public PriceOfferTemplateController(PriceOfferTemplateService service, UserService userService, ModelMapper modelMapper) {
        this.service = service;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    /**
     * List all Price offer templates.
     * @return A list of PriceOfferTemplates
     */
    @GetMapping(path = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferTemplateDTO> getAllTemplates() {
        List<PriceOfferTemplate> all = service.findAll();
        return Arrays.stream(modelMapper.map(all, PriceOfferTemplateDTO[].class)).toList();
    }

    /**
     * Get a specific PriceOfferTemplate
     * @param id the id for the template
     * @return PriceOfferTemplate
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplateDTO getTemplateById(@PathVariable("id") Long id) {
        return modelMapper.map(service.findById(id), PriceOfferTemplateDTO.class);
    }

    /**
     * Create new PriceOfferTemplate
     * @param newTemplateDto the object
     * @return Newly created PriceOfferTemplate object.
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplateDTO createTemplate(@RequestBody PriceOfferTemplateDTO newTemplateDto) {

        log.debug("Create new Price offer template: {}", newTemplateDto);

        List<TemplateMaterial> materials = List.of(modelMapper.map(newTemplateDto.getMaterials(), TemplateMaterial[].class));
        List<TemplateMaterial> zoneBasedMaterials = List.of(modelMapper.map(newTemplateDto.getZoneBasedMaterials(), TemplateMaterial[].class));

        PriceOfferTemplate newTemplate = PriceOfferTemplate.builder()
                .name(newTemplateDto.getName())
                .isShareable(newTemplateDto.getIsShareable())
                .author(userService.findByEmail(newTemplateDto.getAuthor()))
                .sharedWith(userService.findByEmailInList(newTemplateDto.getSharedWith()))
                .materials(materials)
                .zoneBasedMaterials(zoneBasedMaterials)
                .build();

        return modelMapper.map(service.save(newTemplate), PriceOfferTemplateDTO.class);
    }

    /**
     * Update existing PriceOfferTemplate
     * @param priceOfferTemplateDto object with updated values.
     * @return Updated PriceOfferTemplate object.
     */
    @PutMapping(path="save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplateDTO save(@RequestBody PriceOfferTemplateDTO priceOfferTemplateDto) {
        PriceOfferTemplate entity = service.findById(priceOfferTemplateDto.getId());

        List<TemplateMaterial> materials = List.of(modelMapper.map(priceOfferTemplateDto.getMaterials(), TemplateMaterial[].class));
        List<TemplateMaterial> zoneBasedMaterials = List.of(modelMapper.map(priceOfferTemplateDto.getZoneBasedMaterials(), TemplateMaterial[].class));

        entity.setName(priceOfferTemplateDto.getName());
        entity.setIsShareable(priceOfferTemplateDto.getIsShareable());
        entity.setAuthor(userService.findByEmail(priceOfferTemplateDto.getAuthor()));
        entity.setSharedWith(userService.findByEmailInList(priceOfferTemplateDto.getSharedWith()));
        entity.setMaterials(materials);
        entity.setZoneBasedMaterials(zoneBasedMaterials);

        return modelMapper.map(service.save(entity), PriceOfferTemplateDTO.class);
    }

    @ExceptionHandler({PriceOfferTemplateNotFound.class})
    public ResponseEntity<Object> handleNotFoundException() {
        return new ResponseEntity<>("Price Offer Template was not found.", HttpStatus.NOT_FOUND);
    }
}
