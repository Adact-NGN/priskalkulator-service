package no.ding.pk.repository;

import no.ding.pk.domain.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findBySureName(@Param("name") String name);

    @Query(value = "SELECT u FROM User u LEFT JOIN u.salesRole WHERE u.id = :id")
    User findUserByIdWithSalesRole(@Param("id") Long id);
}