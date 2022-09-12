package no.ding.pk.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.User;
import no.ding.pk.repository.UserRepository;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    
    @Autowired
	public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
	public User save(User user) {
		return repository.save(user);
	}

	@Override
	public boolean delete(Long id) {
        if(id == null) {
            return false;
        }

        Optional<User> userToDelete = repository.findById(id);

        if(userToDelete.isPresent()) {
            repository.deleteById(id);
        }

        userToDelete = repository.findById(id);

        if(userToDelete.isPresent()) {
            return false;
        }
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
	public User findUserByIdWithSalesRole(Long id) {
		return repository.findUserByIdWithSalesRole(id);
	}
    
}
