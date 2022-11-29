package no.ding.pk.service;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.User;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findUserByIdWithSalesRole(Long id);
    User save(User user, Long id);
    boolean delete(Long id);
    User removeSalesRoleFromUser(User user);
}
