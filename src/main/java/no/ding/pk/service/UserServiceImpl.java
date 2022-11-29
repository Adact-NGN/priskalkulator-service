package no.ding.pk.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;

@Transactional
@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private UserRepository repository;
	private SalesRoleRepository salesRoleRepository;
	
	
	@Autowired
	public UserServiceImpl(UserRepository repository, SalesRoleRepository salesRoleRepository) {
		this.repository = repository;
		this.salesRoleRepository = salesRoleRepository;
	}
	
	@Override
	public User save(User newUser, Long id) {
		
		if(id == null) {
			log.debug("A new user object was given. Persisting the whole object");
			return repository.save(newUser);
		} else {
			log.debug("Persisting object with id: {}", id);
			Optional<User> optUser = repository.findById(id);
			
			if(!optUser.isPresent()) {
				log.debug("Could not find User object with id: {}", id);
				return null;
			}
			
			User user = optUser.get();
			user.setAdId(newUser.getAdId());
			user.setOrgNr(newUser.getOrgNr());
			user.setOrgName(newUser.getOrgName());
			user.setRegionName(newUser.getRegionName());
			user.setSureName(newUser.getSureName());
			user.setName(newUser.getName());
			user.setUsername(newUser.getUsername());
			user.setUsernameAlias(newUser.getUsernameAlias());
			user.setJobTitle(newUser.getJobTitle());
			user.setFullName(newUser.getFullName());
			user.setResourceNr(newUser.getResourceNr());
			user.setPhoneNumber(newUser.getPhoneNumber());
			user.setEmail(newUser.getEmail());
			user.setAssociatedPlace(newUser.getAssociatedPlace());
			user.setPowerOfAtterneyOA(newUser.getPowerOfAtterneyOA());
			user.setPowerOfAtterneyFA(newUser.getPowerOfAtterneyFA());
			
			user.setOverallPowerOfAtterney(newUser.getOverallPowerOfAtterney());
			user.setEmailSalesManager(newUser.getEmailSalesManager());
			
			user.setRegionalManagersPowerOfAtterney(newUser.getRegionalManagersPowerOfAtterney());
			
			user.setEmailRegionalManager(newUser.getEmailRegionalManager());
			
			user.setDepartment(newUser.getDepartment());
			
			if(newUser.getSalesRole() == null && user.getSalesRole() != null) {
				SalesRole salesRole = user.getSalesRole();
				salesRole.removeUser(user);
				
				salesRoleRepository.save(salesRole);
			} else if(!newUser.getSalesRole().equals(user.getSalesRole())) {
				SalesRole currentSalesRole = user.getSalesRole();
				SalesRole salesRoleFromRequest = getSalesRole(newUser);
				
				if(currentSalesRole != null) {
					currentSalesRole.removeUser(user);
					
					salesRoleRepository.save(currentSalesRole);
				}
				
				salesRoleFromRequest.addUser(user);
				
				salesRoleRepository.save(salesRoleFromRequest);
			}
			
			return repository.save(user);
		}
		
	}
	
	private SalesRole getSalesRole(User user) {
		Optional<SalesRole> optSalesRole = null;
		if(user.getSalesRole() != null) {
			optSalesRole = salesRoleRepository.findById(user.getSalesRole().getId());
			
			if(optSalesRole.isPresent()) {
				return optSalesRole.get();
			}
		}
		return null;
	}
	
	@Override
	public boolean delete(Long id) {
		log.debug("Deleting user with id: {}", id);
		if(id == null) {
			log.debug("Givend id was null. Aborting operation.");
			return false;
		}
		
		Optional<User> userToDelete = repository.findById(id);
		
		if(userToDelete.isPresent()) {
			repository.deleteById(id);
		}
		
		userToDelete = repository.findById(id);
		
		if(userToDelete.isPresent()) {
			log.debug("Could not delete User object");
			return false;
		}

		log.debug("User object with ID: {} was deleted.");
		return true;
	}
	
	@Override
	public List<User> findAll() {
		return repository.findAll();
	}
	
	@Override
	public Optional<User> findById(Long id) {
		return repository.findById(id);
	}
	
	@Override
	public Optional<User> findUserByIdWithSalesRole(Long id) {
		return repository.findById(id);
	}
	
	@Override
	public User removeSalesRoleFromUser(User user) {
		SalesRole userSalesRole = user.getSalesRole();
		
		userSalesRole.removeUser(user);
		
		user = repository.save(user);
		salesRoleRepository.save(userSalesRole);
		return user;
	}
	
}
