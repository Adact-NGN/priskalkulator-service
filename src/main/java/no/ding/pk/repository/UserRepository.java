package no.ding.pk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "User.salesRole")
    List<User> findBySureName(@Param("name") String name);

    @EntityGraph(value = "User.salesRole")
    List<User> findAll();
}