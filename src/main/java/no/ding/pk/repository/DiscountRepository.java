package no.ding.pk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import no.ding.pk.domain.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount>  {
    Discount findBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber);

    List<Discount> findAllBySalesOrg(String salesOrg);

    List<Discount> findAllBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber);
}
