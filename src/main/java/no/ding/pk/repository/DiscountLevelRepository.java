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
    List<DiscountLevel> findBySalesOrgAndMaterialNumberAndLevel(@Param("salesOrg") String salesOrg, @Param("materialNumber") String materialNumber, @Param("level") int level);

    List<DiscountLevel> findAllByParentSalesOrgAndParentMaterialNumberAndLevel(String salesOrg, String materialNumber, Integer level);

    List<DiscountLevel> findAllByParentSalesOrgAndParentMaterialNumber(String salesOrg, String materialNumber);

    List<DiscountLevel> findByParentSalesOrgAndZoneAndParent_MaterialNumberIn(@Param("salesOrg") String salesOrg, @Param("zone") Integer zone, @Param("materialNumbers") List<String> materialNumber);

    @Query("select dl from DiscountLevel dl where dl.parent.salesOrg = :salesOrg and dl.parent.materialNumber IN :materialNumbers")
    List<DiscountLevel> findAllByParentSalesOrgAndParentMaterialNumberInList(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers);

    List<DiscountLevel> findByParentSalesOrgAndParentSalesOfficeAndZoneAndParent_MaterialNumberIn(String salesOrg, String salesOffice, Integer integer, List<String> materialNumberList);

    List<DiscountLevel> findAllByParentSalesOrgAndParentSalesOfficeAndParentMaterialNumberInList(String salesOrg, String salesOffice, List<String> materialNumberList);
}
