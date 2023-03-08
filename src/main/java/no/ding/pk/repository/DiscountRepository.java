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
    Discount findBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber);

    @Query("select distinct d from Discount d JOIN FETCH d.discountLevels where d.salesOrg = :salesOrg")
    List<Discount> findAllBySalesOrg(@Param("salesOrg") String salesOrg);

    @Query("select distinct d from Discount d JOIN FETCH d.discountLevels where d.salesOrg = :salesOrg and d.zone is null and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndMaterialNumberInList(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers);

    @Query("select distinct d from Discount d JOIN FETCH d.discountLevels where d.salesOrg = :salesOrg and d.zone = :zone and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndMaterialNumberAndZoneInListQuery(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers, @Param("zone") String zone);

    @Query("select d from Discount d JOIN FETCH d.discountLevels where d.salesOrg = :salesOrg and d.zone in :zones and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndZoneInAndMaterialNumberIn(@Param("salesOrg") String salesOrg, @Param("zones") List<String> zones, @Param("materialNumbers") List<String> materialNumbers);


}
