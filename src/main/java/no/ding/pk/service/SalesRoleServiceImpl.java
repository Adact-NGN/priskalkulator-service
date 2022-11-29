package no.ding.pk.service;

import static no.ding.pk.repository.specifications.SalesRoleSpecification.hasUserWithId;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.repository.SalesRoleRepository;

@Transactional
@Service
public class SalesRoleServiceImpl implements SalesRoleService {

    private SalesRoleRepository repository;
    
    @Autowired
    public SalesRoleServiceImpl(SalesRoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public SalesRole save(SalesRole salesRole) {
        return repository.save(salesRole);
    }

    @Override
    public List<SalesRole> saveAll(List<SalesRole> salesRoles) {
        return repository.saveAll(salesRoles);
    }

    @Override
    public List<SalesRole> getAllSalesRoles() {
        return repository.findAllWithUserList();
    }

    @Override
    public SalesRole update(Long id, SalesRole salesRole) {
        
        Optional<SalesRole> opt = repository.findById(id);

        if(opt.isPresent()) {
            return repository.save(salesRole);
        }

        return null;
    }

    @Override
    public SalesRole findById(Long id) {
        Optional<SalesRole> optSalesRole = repository.findById(id);

        if(optSalesRole.isPresent()) {
            return optSalesRole.get();
        }

        return null;
    }

    @Override
    public SalesRole findSalesRoleById(Long id) {
        SalesRole salesRole = repository.findByIdWithUserList(id);

        return salesRole;
    }

    @Override
    public SalesRole findSalesRoleByRoleName(String roleName) {
        return repository.findByRoleName(roleName);
    }

    @Override
    public List<SalesRole> findSalesRoleForUser(Long userId) {
        Specification<SalesRole> specification = hasUserWithId(userId);

        return repository.findAll(specification);
    }
}
