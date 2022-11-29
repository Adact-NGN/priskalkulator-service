package no.ding.pk.web.controllers;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.service.SalesRoleService;

@RestController
@RequestMapping("/api/sales-role")
public class SalesRoleController {

    private SalesRoleService service;
    
    @Autowired
    public SalesRoleController(SalesRoleService service) {
        this.service = service;
    }

    /**
     * Get a list of Sales Roles.
     * @return A list of SalesRoles.
     */
    @GetMapping("/list")
    public List<SalesRole> getSalesRoleList() {
        return service.getAllSalesRoles();
    }

    /**
     * Create a new SalesRole object.
     * @param salesRole Data for SalesRole
     * @return Newly created SalesRole object.
     */
    @PostMapping()
    public SalesRole saveSalesRole(@RequestBody SalesRole salesRole) {
        return service.save(salesRole);
    }

    /**
     * Create a list of new SalesRole objects.
     * @param salesRoles List of objects with data for new SalesRole objects.
     * @return A list of all the newly created SalesRole objects.
     */
    @PostMapping("/batch")
    public List<SalesRole> saveBatchSalesRole(@RequestBody List<SalesRole> salesRoles) {
        return service.saveAll(salesRoles);
    }

    /**
     * Update a single SalesRole object.
     * @param salesRole Data to update the SalesRole object with.
     * @return Updated SalesRole object.
     */
    @PutMapping("/{id}")
    public SalesRole updateSalesRole(@RequestBody SalesRole salesRole) {
        return service.save(salesRole);
    }

    /**
     * Get all SalesRole object connected to a user.
     * @param userId The ID to identify the user with.
     * @return A list of SalesRoles connected to the user.
     */
    @GetMapping("/user/{userId}")
    public List<SalesRole> getSalesRoleForUser(@PathVariable("userId") Long userId) {
        return service.findSalesRoleForUser(userId);
    }
}
