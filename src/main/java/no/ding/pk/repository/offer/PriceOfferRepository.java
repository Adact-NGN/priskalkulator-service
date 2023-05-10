package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.PriceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceOfferRepository extends JpaRepository<PriceOffer, Long> {
    @Query("select case when count(po) > 0 then true ELSE false end from PriceOffer po where po.id = :id and po.deleted = true ")
    Boolean existsByIdAndDeleted(@Param("id") Long id);
}
