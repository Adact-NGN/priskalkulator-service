package no.ding.pk.web.controllers.v2;

import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.*;
import no.ding.pk.repository.offer.*;
import no.ding.pk.service.*;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.offer.*;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.sap.StandardPriceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
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
                                               CustomerTermsService customerTermsService,
                                               @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        List<Integer> salesOfficeRequiringOwnFaApproverList = new ArrayList<>();
        salesOfficeRequiringOwnFaApproverList.add(100);
        return new PriceOfferServiceImpl(priceOfferRepository, salesOfficeService, userService, poaService,
                customerTermsService, modelMapper, salesOfficeRequiringOwnFaApproverList);
    }

    @Bean
    public SalesOfficeService salesOfficeService(SalesOfficeRepository salesOfficeRepository,
                                                 PriceRowService priceRowService, ZoneService zoneService,
                                                 StandardPriceService standardPriceService) {
        return new SalesOfficeServiceImpl(salesOfficeRepository,
                priceRowService, zoneService, standardPriceService);
    }

    @Bean
    public MaterialService materialService(MaterialRepository materialRepository, MaterialPriceService materialPriceService) {
        return new MaterialServiceImpl(materialRepository, materialPriceService);
    }

    @Bean
    public InMemory3DCache<String, String, MaterialPrice> materialPriceCache() {
        return new PingInMemory3DCache<>(5000);
    }

    @Bean
    public MaterialPriceService materialPriceService(MaterialPriceRepository materialPriceRepository,
                                                     InMemory3DCache<String, String, MaterialPrice> materialPriceCache) {
        return new MaterialPriceServiceImpl(materialPriceRepository, materialPriceCache);
    }

    @Bean
    public PriceRowService priceRowService(
            DiscountService discountService,
            PriceRowRepository priceRowRepository,
                                           MaterialService materialService,
            EntityManagerFactory emFactory,
                                           SapMaterialService sapMaterialService,
                                           @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        return new PriceRowServiceImpl(discountService, priceRowRepository, materialService, emFactory,
                sapMaterialService, modelMapper);
    }

    @Bean
    public ZoneService zoneService(ZoneRepository zoneRepository,
                                   PriceRowService priceRowService,
                                   StandardPriceService standardPriceService) {
        return new ZoneServiceImpl(zoneRepository, priceRowService, standardPriceService);
    }

    @Bean
    public DiscountService discountService(DiscountRepository discountRepository,
                                           DiscountLevelRepository discountLevelRepository) {
        return new DiscountServiceImpl(discountRepository, discountLevelRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository,
                                   SalesRoleRepository salesRoleRepository) {
        return new UserServiceImpl(userRepository, salesRoleRepository);
    }

    @Bean(name = "sopoaTestService")
    public SalesOfficePowerOfAttorneyService powerOfAttorneyService(SalesOfficePowerOfAttorneyRepository salesOfficePoaRepository) {
        return new SalesOfficePowerOfAttorneyServiceImpl(salesOfficePoaRepository);
    }

    @Bean
    public CustomerTermsService customerTermsService(CustomerTermsRepository customerTermsRepository) {
        return new CustomerTermsServiceImpl(customerTermsRepository);
    }
}

