package no.ding.pk.repository;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query("SELECT sr FROM SalesRole sr LEFT JOIN FETCH sr.userList ul")
    List<SalesRole> findAllWithUserList();

    @Query("SELECT sr FROM SalesRole sr where :user member sr.userList")
    List<SalesRole> findAllByUserListContains(User user);
}
