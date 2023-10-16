package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.PriceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceOfferRepository extends JpaRepository<PriceOffer, Long>, JpaSpecificationExecutor<PriceOffer> {
    @Query("select case when count(po) > 0 then true ELSE false end from PriceOffer po where po.id = :id and po.deleted = true ")
    Boolean existsByIdAndDeleted(@Param("id") Long id);

    List<PriceOffer> findAllByPriceOfferStatusNotIn(List<String> statuses);

    List<PriceOffer> findAllByPriceOfferStatusIn(List<String> statuses);

    List<PriceOffer> findAllBySalesEmployeeId(Long salesEmployeeId);

    List<PriceOffer> findAllByApproverIdAndNeedsApprovalIsTrue(Long approverId);

    PriceOffer findByIdAndApproverIdAndNeedsApprovalIsTrue(Long id, Long approverId);

    List<PriceOffer> findAllByPriceOfferStatusNotLike(String status);

    @Modifying
    @Query("UPDATE PriceOffer po SET po.priceOfferStatus = :priceOfferStatus where po.id = :id")
    int updateStatus(@Param("id") Long id, @Param("priceOfferStatus") String status);
}
