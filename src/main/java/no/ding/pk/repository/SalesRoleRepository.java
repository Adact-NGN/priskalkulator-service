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

    SalesRole findByRoleName(@Param("roleName") String roleName);

}
