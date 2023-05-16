package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;

import no.ding.pk.domain.offer.PriceOffer;

public interface PriceOfferService {

    PriceOffer save(PriceOffer updatedPriceOffer);

    Optional<PriceOffer> findById(Long id);

    List<PriceOffer> findAll();

    boolean delete(Long id);

    List<PriceOffer> findAllBySalesEmployeeId(Long userId);

    List<PriceOffer> findAllByApproverIdAndNeedsApproval(Long approverId);

    Boolean approvePriceOffer(Long priceOfferId, Long approverId, Boolean approved);
}
