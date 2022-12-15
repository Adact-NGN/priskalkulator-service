package no.ding.pk.service;

import java.util.List;

import no.ding.pk.domain.SalesRole;

public interface SalesRoleService {

    SalesRole save(SalesRole salesRole);

    SalesRole update(Long id, SalesRole salesRole);

    SalesRole findById(Long id);

    SalesRole findSalesRoleById(Long id);

    SalesRole findSalesRoleByRoleName(String roleName);

    List<SalesRole> getAllSalesRoles();

    List<SalesRole> saveAll(List<SalesRole> salesRoles);

    List<SalesRole> findSalesRoleForUser(Long userId);
}
