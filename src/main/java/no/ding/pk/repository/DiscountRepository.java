package no.ding.pk.repository;

import no.ding.pk.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount>  {
//    @Query("select d from Discount d where d.salesOrg = :salesOrg and d.materialNumber = :materialNumber and d.discountLevels")
    Discount findBySalesOrgAndMaterialNumberAndDiscountLevelsZone(@Param("salesOrg") String salesOrg, @Param("materialNumber") String materialNumber, @Param("zone") Integer zone);

    List<Discount> findAllBySalesOrg(@Param("salesOrg") String salesOrg);

    List<Discount> findAllBySalesOrgAndDiscountLevelsZoneIsNullAndMaterialNumberIn(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers);

//    @Query("select distinct d from Discount d where d.salesOrg = :salesOrg and d.zone = :zone and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndMaterialNumberInAndDiscountLevelsZone(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers, @Param("zone") Integer zone);

//    @Query("select d from Discount d left join DiscountLevel dl on dl.parent.id = d.id and d.salesOrg = :salesOrg and d.salesOffice = :salesOffice and dl.zone = :zone and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndSalesOfficeAndDiscountLevelsZoneInAndMaterialNumberIn(@Param("salesOrg") String salesOrg, @Param("salesOffice") String salesOffice, @Param("zones") List<Integer> zones, @Param("materialNumbers") List<String> materialNumbers);

    List<Discount> findAllBySalesOrgAndSalesOfficeAndMaterialNumberIn(String salesOrg, String salesOffice, List<String> materials);
}
