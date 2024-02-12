package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.web.dto.web.client.SalesRoleDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tag(name = "SalesRoleController", description = "Get sales roles for users")
@RestController
@RequestMapping({"/api/sales-role", "/api/v1/sales-role"})
public class SalesRoleController {

    private final Logger log = LoggerFactory.getLogger(SalesRoleController.class);

    private final SalesRoleService service;

    private final ModelMapper modelMapper;

    @Autowired
    public SalesRoleController(SalesRoleService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    /**
     * Get a list of Sales Roles.
     * @return A list of SalesRoles.
     */
    @Operation(
            summary = "SalesRole - Get all sales roles",
            description = "Get a list of Sales roles.",
            method = "GET",
            tags = "SalesRoleController"
    )
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesRoleDTO> getSalesRoleList() {
        log.debug("Getting Sales Role list");
        List<SalesRole> salesRoles = service.getAllSalesRoles();

        log.debug("Returning {} amount of roles.", salesRoles.size());
        return salesRoles.stream().map(mapToDTO()).collect(Collectors.toList());
    }

    /**
     * Create a new SalesRole object.
     * @param salesRole Data for SalesRole
     * @return Newly created SalesRole object.
     */
    @Operation(
            summary = "SalesRole - Create sales role",
            description = "Create new Sales role.",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "SalesRoleDTO"),
            tags = "SalesRoleController"
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesRoleDTO saveSalesRole(@RequestBody SalesRole salesRole) {
        return modelMapper.map(service.save(salesRole), SalesRoleDTO.class);
    }

    /**
     * Create a list of new SalesRole objects.
     * @param salesRoles List of objects with data for new SalesRole objects.
     * @return A list of all the newly created SalesRole objects.
     */
    @Operation(
            summary = "SalesRole - Create a list of sales roles.",
            description = "Create a list of Sales roles.",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "SalesRoleDTO"),
            tags = "SalesRoleController"
    )
    @PostMapping(path = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesRoleDTO> saveBatchSalesRole(@RequestBody List<SalesRole> salesRoles) {
        return service.saveAll(salesRoles).stream().map(mapToDTO()).collect(Collectors.toList());
    }

    /**
     * Update a single SalesRole object.
     * @param salesRole Data to update the SalesRole object with.
     * @return Updated SalesRole object.
     */
    @Operation(
            summary = "SalesRole - Update a single SalesRole object",
            method = "PUT",
            parameters = {
                @Parameter(name = "id",
                        description = "Numeric ID for the SalesRole to get",
                        required = true
                )
            },
            tags = "SalesRoleController")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SalesRoleDTO updateSalesRole(@PathVariable("id") Long id, @RequestBody SalesRole salesRole) {
        return modelMapper.map(service.save(salesRole), SalesRoleDTO.class);
    }

    /**
     * Get all SalesRole object connected to a user.
     * @param userId The ID to identify the user with.
     * @return A list of SalesRoles connected to the user.
     */
    @Operation(
            summary = "SalesRole - Get all sales roles for a user.",
            description = "SalesRole Get all SalesRole object connected to a user.",
            method = "GET",
            parameters = @Parameter(name = "userId", description = "ID for user to get sales roles for.", required = true),
            tags = "SalesRoleController"
    )
    @GetMapping(path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesRoleDTO> getSalesRoleForUser(@PathVariable("userId") Long userId) {
        return service.findSalesRoleForUser(userId).stream().map(mapToDTO()).collect(Collectors.toList());
    }

    private Function<SalesRole, SalesRoleDTO> mapToDTO() {
        return salesRole -> modelMapper.map(salesRole, SalesRoleDTO.class);
    }
}
