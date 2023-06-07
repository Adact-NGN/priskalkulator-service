package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTerms;

import java.util.List;
import java.util.Optional;

public interface PriceOfferService {

    PriceOffer save(PriceOffer updatedPriceOffer);

    Optional<PriceOffer> findById(Long id);

    List<PriceOffer> findAll();

    boolean delete(Long id);

    List<PriceOffer> findAllBySalesEmployeeId(Long userId);

    List<PriceOffer> findAllByApproverIdAndPriceOfferStatus(Long approverId, String priceOfferStatus);

    Boolean approvePriceOffer(Long priceOfferId, Long approverId, String priceOfferStatus, String comment);

    Boolean activatePriceOffer(Long approverId, Long priceOfferId, PriceOfferTerms customerTerms);
}
