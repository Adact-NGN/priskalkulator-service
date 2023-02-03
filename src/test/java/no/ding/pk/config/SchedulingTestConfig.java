package no.ding.pk.config;

import static org.mockito.Mockito.mock;

import no.ding.pk.service.converters.PdfService;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.template.HandlebarsTemplateService;
import no.ding.pk.service.template.HandlebarsTemplateServiceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;

import no.ding.pk.repository.DiscountLevelRepository;
import no.ding.pk.repository.DiscountRepository;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.repository.offer.CustomerTermsRepository;
import no.ding.pk.repository.offer.MaterialPriceRepository;
import no.ding.pk.repository.offer.MaterialRepository;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.repository.offer.PriceOfferTemplateRepository;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.repository.offer.SalesOfficeRepository;
import no.ding.pk.repository.offer.ZoneRepository;
import no.ding.pk.service.InMemoryCache;
import no.ding.pk.service.MaterialInMemoryCache;
import no.ding.pk.web.mappers.MapperService;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableScheduling
@Profile({ "unit-test" })
@ComponentScan("no.ding.pk.service")
public class SchedulingTestConfig {

    @Bean
    public InMemoryCache<String, String, String> inMemoryCache() {
        return new MaterialInMemoryCache<>();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return mock(ObjectMapper.class);
    }

    @Bean
    public ConfidentialClientApplication ccapp() {
        return mock(ConfidentialClientApplication.class);
    }

    @Bean
    public ModelMapper modelMapper() {
        return mock(ModelMapper.class);
    }

    @Bean
    public MapperService mapperService() {
        return mock(MapperService.class);
    }

    @Bean
    public SapMaterialService materialService() {
        return mock(SapMaterialService.class);
    }

    @Bean
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    public DiscountRepository discountRepository() {
        return mock(DiscountRepository.class);
    }

    @Bean
    public DiscountLevelRepository discountLevelRepository() {
        return mock(DiscountLevelRepository.class);
    }

    @Bean
    public SalesRoleRepository salesRoleRepository() {
        return mock(SalesRoleRepository.class);
    }

    @Bean
    public PriceOfferRepository priceOfferRepository() {
        return mock(PriceOfferRepository.class);
    }

    @Bean
    public PriceOfferTemplateRepository priceOfferTemplateRepository() {
        return mock(PriceOfferTemplateRepository.class);
    }

    @Bean
    public SalesOfficeRepository salesOfficeRepository() {
        return mock(SalesOfficeRepository.class);
    }

    @Bean
    public CustomerTermsRepository customerTermsRepository() {
        return mock(CustomerTermsRepository.class);
    }

    @Bean
    public MaterialPriceRepository materialPriceRepository() {
        return mock(MaterialPriceRepository.class);
    }

    @Bean
    public MaterialRepository materialRepository() {
        return mock(MaterialRepository.class);
    }

    @Bean
    public PriceRowRepository priceRowRepository() {
        return mock(PriceRowRepository.class);
    }

    @Bean
    public ZoneRepository zoneRepository() {
        return mock(ZoneRepository.class);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return mock(EntityManagerFactory.class);
    }

    @Bean
    public HandlebarsTemplateService handlebarsTemplateService() {
        return new HandlebarsTemplateServiceImpl("priceOfferTemplate");
    }

    @Bean
    public PdfService pdfService() {
        return mock(PdfService.class);
    }
}
