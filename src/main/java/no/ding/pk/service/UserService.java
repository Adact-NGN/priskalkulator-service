package no.ding.pk.service;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.User;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User findUserByIdWithSalesRole(Long id);
    User save(User user);
    boolean delete(Long id);
}
