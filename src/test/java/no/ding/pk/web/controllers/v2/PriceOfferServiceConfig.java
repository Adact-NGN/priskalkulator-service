package no.ding.pk.web.controllers.v2;

import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.DiscountService;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.UserService;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.service.offer.PriceOfferServiceImpl;
import no.ding.pk.service.offer.SalesOfficeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@Import({ModelMapperV2Config.class})
@Configuration
public class PriceOfferServiceConfig {

    @Bean
    public PriceOfferService priceOfferService(PriceOfferRepository priceOfferRepository,
                                               SalesOfficeService salesOfficeService,
                                               UserService userService,
                                               SalesOfficePowerOfAttorneyService poaService,
                                               DiscountService discountService,
                                               CustomerTermsService customerTermsService,
                                               @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        List<Integer> salesOfficeRequiringOwnFaApproverList = new ArrayList<>();
        salesOfficeRequiringOwnFaApproverList.add(100);
        return new PriceOfferServiceImpl(priceOfferRepository, salesOfficeService, userService, poaService,
                discountService, customerTermsService, modelMapper, salesOfficeRequiringOwnFaApproverList);
    }
}
