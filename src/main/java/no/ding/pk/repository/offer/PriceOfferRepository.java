package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.PriceOffer;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

@Repository
public interface PriceOfferRepository extends JpaRepository<PriceOffer, Long>, JpaSpecificationExecutor<PriceOffer> {
    @Query("select case when count(po) > 0 then true ELSE false end from PriceOffer po where po.id = :id and po.deleted = true ")
    Boolean existsByIdAndDeleted(@Param("id") Long id);

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Query("select po from PriceOffer po")
    Stream<PriceOffer> findAllAsStream();

    List<PriceOffer> findAllByPriceOfferStatusNotIn(List<String> statuses);

    List<PriceOffer> findAllByPriceOfferStatusIn(List<String> statuses);

    List<PriceOffer> findAllBySalesEmployeeId(Long salesEmployeeId);

    List<PriceOffer> findAllByApproverIdAndNeedsApprovalIsTrue(Long approverId);

    PriceOffer findByIdAndApproverIdAndNeedsApprovalIsTrue(Long id, Long approverId);

    List<PriceOffer> findAllByPriceOfferStatusNotLike(String status);

    @Modifying
    @Query("UPDATE PriceOffer po SET po.priceOfferStatus = :priceOfferStatus where po.id = :id")
    int updateStatus(@Param("id") Long id, @Param("priceOfferStatus") String status);

    @Query("select distinct po from PriceOffer po inner join po.salesOfficeList so where so.salesOffice in :salesOfficeNumbers")
    List<PriceOffer> findAllBySalesOfficeInList(@Param("salesOfficeNumbers") List<String> salesOfficeNumbers);

    @Modifying
    @Query("UPDATE PriceOffer po SET po.customerNumber = :customerNumber where po.id = :id")
    void updateCustomerNumber(@Param("id") Long id, @Param("customerNumber") String customerNumber);
}
