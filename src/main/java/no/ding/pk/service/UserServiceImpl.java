package no.ding.pk.service;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private final UserRepository repository;
	private final SalesRoleRepository salesRoleRepository;
	
	
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
			
			if(optUser.isEmpty()) {
				log.debug("Could not find User object with id: {}", id);
				return null;
			}
			
			return repository.save(newUser);
		}
	}
	
	private void setSalesRole(SalesRole salesRole, User user) {
		SalesRole currentSalesRole = user != null && user.getSalesRole() != null ? user.getSalesRole() : null;
		if(currentSalesRole != null) {
			currentSalesRole = salesRoleRepository.findByRoleName(currentSalesRole.getRoleName());
		}
		SalesRole newSalesRole = salesRole;
		if(newSalesRole != null) {
			newSalesRole = salesRoleRepository.findByRoleName(newSalesRole.getRoleName());
		}

		if(currentSalesRole != null) {
			currentSalesRole.removeUser(user);

			salesRoleRepository.save(currentSalesRole);
		}

		if(newSalesRole != null && user != null) {
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
			log.debug("Given id was null. Aborting operation.");
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
		
		log.debug("User object with ID: {} was deleted.", id);
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
	public User updateSalesRoleForUser(User user, SalesRole salesRole) {
		if(user.getSalesRole() == null) {
			salesRole.addUser(user);

			return repository.save(user);
		}

		if(Objects.equals(user.getSalesRole(), salesRole)) {
			return user;
		}

		if(!Objects.equals(user.getSalesRole(), salesRole)) {
			user = removeSalesRoleFromUser(user);

			salesRole.addUser(user);

			return repository.save(user);
		}
		return user;
	}

	@Override
	public User findByEmail(String employeeEail) {
		log.debug("Search for User with email: {}", employeeEail);
		return repository.findByEmailIgnoreCase(employeeEail);
	}

	@Override
	public List<User> findByEmailInList(List<String> superAdmins) {
		return repository.findAllByEmailIn(superAdmins);
	}

}
