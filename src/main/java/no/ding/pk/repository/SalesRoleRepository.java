package no.ding.pk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;

@Repository
public interface SalesRoleRepository extends JpaRepository<SalesRole, Long>, JpaSpecificationExecutor<SalesRole> {

    @Query("select sr from SalesRole sr LEFT JOIN FETCH sr.userList ul where sr.roleName = :roleName")
    SalesRole findByRoleName(@Param("roleName") String roleName);

    @EntityGraph(value = "SalesRole.userList")
    @Query("select sr from SalesRole sr LEFT JOIN FETCH sr.userList ul where sr.id = :id")
    SalesRole findByIdWithUserList(@Param("id") Long id);

    @EntityGraph(value = "SalesRole.userList")
    @Query("select sr.userList from SalesRole sr where sr.roleName = :roleName")
    List<User> findAllUsersByRoleName(@Param("roleName") String roleName);

    // @EntityGraph(value = "SalesRole.userList")
    @Query("SELECT sr FROM SalesRole sr LEFT JOIN FETCH sr.userList ul")
    List<SalesRole> findAllWithUserList();

    @Query("SELECT sr FROM SalesRole sr LEFT JOIN FETCH sr.userList ul")
    Optional<SalesRole> findById(@Param("id") Long id);
}
