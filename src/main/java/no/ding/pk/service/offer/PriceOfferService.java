package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTerms;

import java.util.List;
import java.util.Optional;

public interface PriceOfferService {

    PriceOffer save(PriceOffer updatedPriceOffer);

    Optional<PriceOffer> findById(Long id);

    List<PriceOffer> findAll();

    List<PriceOffer> findAllWithoutStatusInList(List<String> status);

    boolean delete(Long id);

    List<PriceOffer> findAllBySalesEmployeeId(Long userId, List<String> statusList);

    List<PriceOffer> findAllByApproverIdAndPriceOfferStatus(Long approverId, String priceOfferStatus);

    Boolean approvePriceOffer(Long priceOfferId, Long approverId, String priceOfferStatus, String comment);

    Boolean activatePriceOffer(Long activatedById, Long priceOfferId, PriceOfferTerms customerTerms, String generalComment);

    List<PriceOffer> findAllByPriceOfferStatusInList(List<String> statusList);

    List<PriceOffer> findAllPriceOffersRadyForBoReport();

    void updateStatus(Long id, String status);

    List<PriceOffer> findAllBySalesOfficeAndStatus(List<String> salesOffices, List<String> statuses);

    void updateCustomerNumber(Long id, String customerNumber);

    PriceOffer updatePriceOffer(PriceOffer updatedOffer);
}
