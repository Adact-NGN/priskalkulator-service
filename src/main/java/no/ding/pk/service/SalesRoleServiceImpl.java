package no.ding.pk.service;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.repository.SalesRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static no.ding.pk.repository.specifications.SalesRoleSpecification.hasUserWithId;

@Transactional
@Service
public class SalesRoleServiceImpl implements SalesRoleService {

    private static final Logger log = LoggerFactory.getLogger(SalesRoleServiceImpl.class);

    private final SalesRoleRepository repository;

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
        return repository.findAll();
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

        return optSalesRole.orElse(null);

    }

    @Override
    public SalesRole findSalesRoleById(Long id) {
        Optional<SalesRole> salesRole = repository.findById(id);

        return salesRole.orElse(null);
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
