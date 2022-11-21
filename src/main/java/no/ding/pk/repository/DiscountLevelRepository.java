package no.ding.pk.repository;

import org.springframework.stereotype.Repository;

import no.ding.pk.domain.DiscountLevel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DiscountLevelRepository extends JpaRepository<DiscountLevel, Long> {
    @Query("select dl from DiscountLevel dl where dl.level = :level and dl.parent.salesOrg = :salesOrg and dl.parent.materialNumber = :materialNumber")
    List<DiscountLevel> findBySalesOrgAndMaterialNumberAndLevel(@Param("salesOrg") String salesOrg, 
    @Param("materialNumber") String materialNumber, 
    @Param("level") int level);

    List<DiscountLevel> findAllByParentSalesOrgAndParentMaterialNumberAndLevel(String salesOrg, String materialNumber, int level);

    List<DiscountLevel> findAllByParentSalesOrgAndParentMaterialNumber(String salesOrg, String materialNumber);
}
