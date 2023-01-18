package no.ding.pk.service;

import static no.ding.pk.repository.specifications.SalesRoleSpecification.hasUserWithId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;

@Transactional
@Service
public class SalesRoleServiceImpl implements SalesRoleService {

    private static final Logger log = LoggerFactory.getLogger(SalesRoleServiceImpl.class);

    private SalesRoleRepository repository;
    private UserService userService;
    
    @Autowired
    public SalesRoleServiceImpl(SalesRoleRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public SalesRole save(SalesRole salesRole) {
//        SalesRole entity = repository.findByRoleName(salesRole.getRoleName());
//
//        if(entity == null) {
//            return repository.save(salesRole);
//        }
//
//        entity = new SalesRole();
//        entity.setDefaultPowerOfAttorneyFa(salesRole.getDefaultPowerOfAttorneyFa());
//        entity.setDefaultPowerOfAttorneyOa(salesRole.getDefaultPowerOfAttorneyOa());
//        entity.setDescription(salesRole.getDescription());
//        entity.setRoleName(salesRole.getDescription());
//
//        if(salesRole.getUserList() == null) {
//            salesRole.setUserList(new ArrayList<>());
//        }
//        List<User> userList = new ArrayList<>();
//        for(int i = 0; i < salesRole.getUserList().size(); i++) {
//            User user = salesRole.getUserList().get(i);
//
//            if(user.getId() != null) {
//                Optional<User> optUser = userService.findById(user.getId());
//
//                if(optUser.isPresent()) {
//                    user = optUser.get();
//                    userList.add(user);
//                } else {
//                    log.error("Non existing user found in request, with data: {}", user.toString());
//                }
//            }
//        }
//
//        entity.setUserList(userList);

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
