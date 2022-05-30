package no.ding.pk.repository;

import no.ding.pk.domain.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findBySureName(@Param("name") String name);
}