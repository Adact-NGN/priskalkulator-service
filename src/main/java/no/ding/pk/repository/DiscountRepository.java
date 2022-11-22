package no.ding.pk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount>  {
    Discount findBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber);

    List<Discount> findAllBySalesOrg(String salesOrg);

    @Query("select distinct d from Discount d JOIN FETCH d.discountLevels where d.salesOrg = :salesOrg and d.materialNumber in :materialNumbers")
    List<Discount> findAllBySalesOrgAndMaterialNumberInList(@Param("salesOrg") String salesOrg, @Param("materialNumbers") List<String> materialNumbers);
}
