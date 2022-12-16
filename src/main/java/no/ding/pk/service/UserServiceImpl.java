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
			
			User user = repository.save(newUser);
			
			setSalesRole(newUser.getSalesRole(), user);
			
			return repository.save(user);
		} else {
			log.debug("Persisting object with id: {}", id);
			Optional<User> optUser = repository.findById(id);
			
			if(!optUser.isPresent()) {
				log.debug("Could not find User object with id: {}", id);
				return null;
			}
			
			User user = optUser.get();
			user.copy(newUser);
			
			setSalesRole(newUser.getSalesRole(), user);
			
			return repository.save(user);
		}
		
	}
	
	private void setSalesRole(SalesRole salesRole, User user) {
		SalesRole currentSalesRole = user != null && user.getSalesRole() != null ? user.getSalesRole() : null;
		if(currentSalesRole != null) {
			currentSalesRole = salesRoleRepository.findByRoleName(currentSalesRole.getRoleName());
		}
		SalesRole newSalesRole = salesRole != null ? salesRole : null;
		if(newSalesRole != null) {
			newSalesRole = salesRoleRepository.findByRoleName(newSalesRole.getRoleName());
		}

		if(currentSalesRole != null) {
			currentSalesRole.removeUser(user);

			salesRoleRepository.save(currentSalesRole);
		}

		if(newSalesRole != null) {
			newSalesRole.addUser(user);

			salesRoleRepository.save(newSalesRole);
		}
	}
	
	private SalesRole getSalesRole(User user) {
		SalesRole salesRole = null;
		if(user.getSalesRole() != null) {
			salesRole = salesRoleRepository.findByRoleName(user.getSalesRole().getRoleName());
		}
		return salesRole;
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

	@Override
	public User findByEmail(String employeeEail) {
		log.debug("Search for User with email: {}", employeeEail);
		return repository.findByEmailIgnoreCase(employeeEail);
	}
	
}
