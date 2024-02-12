package no.ding.pk.web.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "SalesOfficePowerOfAttorneyController", description = "Controller for handling authorization matrix.")
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

    /**
     * Get power of attorney by id
     * @param id power of attorney id
     * @return {@code SalesOfficePowerOfAttorneyDTO} object
     */
    @Operation(summary = "SalesOfficePowerOfAttorney - Get sopoa by ID",
            description = "Get power of attorney by ID",
            method = "GET",
            parameters = {
                    @Parameter(name = "id", description = "ID for power of attorney",required = true)
            },
            tags = "SalesOfficePowerOfAttorneyController"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ID for power of attorney", ref = "SalesOfficePowerOfAttorneyDTO")
    })
    @GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesOfficePowerOfAttorneyDTO byId(@PathVariable("id") Long id) {
        log.debug("Getting entity for id {}", id);

        PowerOfAttorney poa = service.findById(id);

        if(poa != null) {
            return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
        }

        return null;
    }

    /**
     * Get power of attorney by sales office number
     * @param salesOfficeNumber Sales office number to look up
     * @return SalesOffice object or null
     */
    @Operation(summary = "SalesOfficePowerOfAttorney - Get by sales office",
            description = "Get power of attorney by sales office number",
            method = "GET",
            parameters = {
                    @Parameter(name = "salesOfficeNubmer", description = "Sales office number to look up", required = true)
            },
            tags = "SalesOfficePowerOfAttorneyController"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales office object or null", ref = "SalesOfficePowerOfAttorneyDTO")
    })
    @GetMapping(path = "/office/{salesOfficeNumber}")
    public SalesOfficePowerOfAttorneyDTO bySalesOfficeNumber(@PathVariable("salesOfficeNumber") Integer salesOfficeNumber) {

        log.debug("Getting entity by sales office number: {}", salesOfficeNumber);

        PowerOfAttorney poa = service.findBySalesOffice(salesOfficeNumber);

        if(poa != null) {
            return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
        }

        return null;
    }

    /**
     * Get all power og attorney objects
     * @return List of {@code SalesOfficePowerOfAttorneyDTO}
     */
    @Operation(summary = "SalesOfficePowerOfAttorney - Get all sopoa",
            description = "Get all power of attorneys",
            method = "GET",
            tags = "SalesOfficePowerOfAttorneyController"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of power of attorney objects", ref = "SalesOfficePowerOfAttorneyDTO")
    })
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesOfficePowerOfAttorneyDTO> list() {
        log.debug("Received request for all PowerOfAttorneys");
        List<PowerOfAttorney> list = service.findAll();

        return list.stream().map(poa -> modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class)).collect(Collectors.toList());
    }

    /**
     * Create new power of attorney object
     * @param sopa {@code SalesOfficePowerOfAttorneyDTO} power of attorney values
     * @return Newlye persisted power of attorney as {@code SalesOfficePowerOfAttorneyDTO}
     */
    @Operation(summary = "SalesOfficePowerOfAttorney - Create new",
            description = "Create new power of attorney",
            method = "POST",
            tags = "SalesOfficePowerOfAttorneyController"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales office object or null", ref = "SalesOfficePowerOfAttorneyDTO")
    })
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesOfficePowerOfAttorneyDTO create(@RequestBody SalesOfficePowerOfAttorneyDTO sopa) {
        log.debug("Creating new Power of Attorney");

        PowerOfAttorney poa = modelMapper.map(sopa, PowerOfAttorney.class);

        poa = service.save(poa);

        return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
    }

    /**
     * Updated power of attorney object
     * @param id Power of attorney id
     * @param sopoaDTO Updated power of attorney object.
     * @return {@code SalesOfficePowerOfAttorneyDTO} Updated power of attorney object
     */
    @Operation(summary = "SalesOfficePowerOfAttorney - Update sopoa",
            description = "Update power of attorney object",
            method = "PUT",
            parameters = @Parameter(name = "id", description = "ID for sales office to update"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "SalesOfficePowerOfAttorneyDTO", required = true),
            tags = "SalesOfficePowerOfAttorneyController"
    )
    @PutMapping(path = "/save/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesOfficePowerOfAttorneyDTO save(@PathVariable("id") Long id, @RequestBody SalesOfficePowerOfAttorneyDTO sopoaDTO) {
        log.debug("Trying to update poa with id {}", id);
        log.debug("With values {}", sopoaDTO);

        PowerOfAttorney poa = modelMapper.map(sopoaDTO, PowerOfAttorney.class);

        log.debug("Mapped object: {}", poa);
        poa = service.save(poa);

        log.debug("Persisted object: {}", poa);

        return modelMapper.map(poa, SalesOfficePowerOfAttorneyDTO.class);
    }

    /**
     * Delete power of attorney by id
     * @param id Power of attorney id to delete
     * @return true if deleted, else false
     */
    @Operation(summary = "SalesOfficePowerOfAttorney - Delete sopoa",
            description = "Delete power of attorney by id",
            method = "DELETE",
            parameters = @Parameter(name = "id", description = "ID for power of attorney to delete"),
            tags = "SalesOfficePowerOfAttorneyController"
    )
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean delete(@PathVariable("id") Long id) {
        log.debug("Trying to delete entity with id {}", id);

        return service.delete(id);
    }

}
