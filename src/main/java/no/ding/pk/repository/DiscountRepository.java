package no.ding.pk.repository;

import no.ding.pk.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount>  {
    Discount findBySalesOrgAndMaterialNumberAndZone(String salesOrg, String materialNumber, String zone);

    @Query("select distinct d from Discount d where d.salesOrg = :salesOrg")
    List<Discount> findAllBySalesOrg(@Param("salesOrg") String salesOrg);

    List<Discount> findAllBySalesOrgAndZoneIsNullAndMaterialNumberIn(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers);

    @Query("select distinct d from Discount d where d.salesOrg = :salesOrg and d.zone = :zone and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndMaterialNumberAndZoneInListQuery(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers, @Param("zone") String zone);

    List<Discount> findAllBySalesOrgAndSalesOfficeAndZoneInAndMaterialNumberIn(@Param("salesOrg") String salesOrg, @Param("salesOffice") String salesOffice, @Param("zones") List<String> zones, @Param("materialNumbers") List<String> materialNumbers);

    List<Discount> findAllBySalesOrgAndSalesOfficeAndMaterialNumberIn(String salesOrg, String salesOffice, List<String> materials);
}
