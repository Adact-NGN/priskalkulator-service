package no.ding.pk.web.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.domain.offer.template.TemplateMaterial;
import no.ding.pk.service.UserService;
import no.ding.pk.service.offer.PriceOfferTemplateService;
import no.ding.pk.web.dto.web.client.offer.template.PriceOfferTemplateDTO;
import no.ding.pk.web.handlers.MissingRequiredPropertyException;
import no.ding.pk.web.handlers.PriceOfferTemplateNotFound;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

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
    public PriceOfferTemplateController(PriceOfferTemplateService service,
                                        UserService userService,
                                        @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        this.service = service;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    /**
     * List all Price offer templates.
     * @return A list of PriceOfferTemplates
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PriceOfferTemplateDTO> getAllTemplates(@RequestParam(value = "user", required = false) String userEmail) {

        if(StringUtils.isNotBlank(userEmail)) {
            List<PriceOfferTemplate> all = service.findAllByAuthor(userEmail);
            return Arrays.stream(modelMapper.map(all, PriceOfferTemplateDTO[].class)).toList();
        }
        List<PriceOfferTemplate> all = service.findAll();
        return Arrays.stream(modelMapper.map(all, PriceOfferTemplateDTO[].class)).toList();
    }

    /**
     * List all PriceOfferTemplates shared with user by email
     * @param sharedWithEmail User email
     * @return A list of PriceOfferTemplates
     */
    @Operation(summary = "List all price offer templates shared with user by email",
            parameters = {@Parameter(name = "sharedWithEmail", required = true, description = "User email", example = "test.testesen@testing.no")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns a list of shared price offer templates")
    })
    @GetMapping(path = "/list/shared/{sharedWithEmail}")
    public List<PriceOfferTemplateDTO> getAllTemplatesSharedWith(@PathVariable("sharedWithEmail") String sharedWithEmail) {
        List<PriceOfferTemplate> sharedWithUser = service.findAllSharedWithUser(sharedWithEmail);

        return Arrays.stream(modelMapper.map(sharedWithUser, PriceOfferTemplateDTO[].class)).toList();
    }

    /**
     * Get a specific PriceOfferTemplate
     * @param id the id for the template
     * @return PriceOfferTemplate
     */
    @Operation(summary = "Get a specific price offer template")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplateDTO getTemplateById(@PathVariable("id") Long id) {
        return modelMapper.map(service.findById(id), PriceOfferTemplateDTO.class);
    }

    /**
     * Create new PriceOfferTemplate
     * @param newTemplateDto the object
     * @return Newly created PriceOfferTemplate object.
     */
    @Operation(summary = "Create a new price offer template",
            description = "Creates a new price offer template with the given request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns newly created price offer template"),
            @ApiResponse(responseCode = "400", description = "Bad request, missing name for template.")
    })
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplateDTO createTemplate(@RequestBody PriceOfferTemplateDTO newTemplateDto) {

        log.debug("Create new Price offer template: {}", newTemplateDto);

        if(StringUtils.isBlank(newTemplateDto.getName())) {
            throw new MissingRequiredPropertyException("PriceOfferTemplate.name property is missing.");
        }

        PriceOfferTemplate.PriceOfferTemplateBuilder newTemplate = PriceOfferTemplate.builder()
                .name(newTemplateDto.getName())
                .isShareable(newTemplateDto.getIsShareable());

        if(StringUtils.isNotBlank(newTemplateDto.getAuthor())) {
            newTemplate.author(userService.findByEmail(newTemplateDto.getAuthor()));
        }

        if(newTemplateDto.getSharedWith() != null && !newTemplateDto.getSharedWith().isEmpty()){
            newTemplate.sharedWith(userService.findByEmailInList(newTemplateDto.getSharedWith()));
        }

        if(newTemplateDto.getMaterials() != null) {
            List<TemplateMaterial> materials = List.of(modelMapper.map(newTemplateDto.getMaterials(), TemplateMaterial[].class));
            newTemplate.materials(materials);
        }

        if(newTemplateDto.getZoneBasedMaterials() != null) {
            List<TemplateMaterial> zoneBasedMaterials = List.of(modelMapper.map(newTemplateDto.getZoneBasedMaterials(), TemplateMaterial[].class));
            newTemplate.zoneBasedMaterials(zoneBasedMaterials);
        }

        return modelMapper.map(service.save(newTemplate.build()), PriceOfferTemplateDTO.class);
    }

    /**
     * Update existing PriceOfferTemplate
     * @param priceOfferTemplateDto object with updated values.
     * @return Updated PriceOfferTemplate object.
     */
    @Operation(description = "Update existing price offer template with new values.",
    summary = "Update price offer template",
            parameters = @Parameter(name = "id", description = "Price offer templates id", required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated price offer template")
    })
    @PutMapping(path="/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PriceOfferTemplateDTO save(@PathVariable("id") Long id,
                                      @RequestBody PriceOfferTemplateDTO priceOfferTemplateDto) {

        if(StringUtils.isBlank(priceOfferTemplateDto.getName())) {
            throw new MissingRequiredPropertyException("PriceOfferTemplate.name property is missing.");
        }

        if(id == null) {
            throw new MissingRequiredPropertyException("PriceOfferTemplate.id property is missing.");
        }
        PriceOfferTemplate entity = service.findById(id);

        entity.setName(priceOfferTemplateDto.getName());
        entity.setIsShareable(priceOfferTemplateDto.getIsShareable());

        if(StringUtils.isNotBlank(priceOfferTemplateDto.getAuthor())) {
            entity.setAuthor(userService.findByEmail(priceOfferTemplateDto.getAuthor()));
        }

        if(priceOfferTemplateDto.getSharedWith() != null && !priceOfferTemplateDto.getSharedWith().isEmpty()) {
            entity.setSharedWith(userService.findByEmailInList(priceOfferTemplateDto.getSharedWith()));
        }

        if(priceOfferTemplateDto.getMaterials() != null && !priceOfferTemplateDto.getMaterials().isEmpty()) {
            List<TemplateMaterial> materials = List.of(modelMapper.map(priceOfferTemplateDto.getMaterials(), TemplateMaterial[].class));
            entity.setMaterials(materials);
        }

        if(priceOfferTemplateDto.getZoneBasedMaterials() != null && !priceOfferTemplateDto.getZoneBasedMaterials().isEmpty()) {
            List<TemplateMaterial> zoneBasedMaterials = List.of(modelMapper.map(priceOfferTemplateDto.getZoneBasedMaterials(), TemplateMaterial[].class));
            entity.setZoneBasedMaterials(zoneBasedMaterials);
        }

        return modelMapper.map(service.save(entity), PriceOfferTemplateDTO.class);
    }

    @ExceptionHandler({PriceOfferTemplateNotFound.class})
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>("Price Offer Template was not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MissingRequiredPropertyException.class})
    public ResponseEntity<Object> handleMissingPropertyException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
