package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PriceOfferMapper {
    PriceOfferDTO priceOfferToPriceOfferDTO(PriceOffer priceOffer);
}
