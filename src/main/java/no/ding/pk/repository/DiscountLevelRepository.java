package no.ding.pk.repository;

import no.ding.pk.domain.DiscountLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountLevelRepository extends JpaRepository<DiscountLevel, Long> {

    List<DiscountLevel> findAllByParentSalesOrgAndParentMaterialNumberAndLevel(String salesOrg, String materialNumber, Integer level);

    List<DiscountLevel> findByParentSalesOrgAndParentSalesOfficeAndZoneAndParent_MaterialNumberIn(String salesOrg, String salesOffice, Integer integer, List<String> materialNumberList);

    List<DiscountLevel> findAllByParentSalesOrgAndParentSalesOfficeAndParentMaterialNumberIn(String salesOrg, String salesOffice, List<String> materialNumberList);
}
