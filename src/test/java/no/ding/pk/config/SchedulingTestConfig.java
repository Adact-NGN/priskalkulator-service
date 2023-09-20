package no.ding.pk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import no.ding.pk.repository.DiscountLevelRepository;
import no.ding.pk.repository.DiscountRepository;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.repository.offer.*;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.converters.PdfService;
import no.ding.pk.service.offer.PriceRowService;
import no.ding.pk.service.offer.SalesOfficeService;
import no.ding.pk.service.template.HandlebarsTemplateService;
import no.ding.pk.service.template.HandlebarsTemplateServiceImpl;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.mappers.MapperService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.persistence.EntityManagerFactory;

import static org.mockito.Mockito.mock;

@Configuration
@Profile({ "unit-test" })
@ComponentScan("no.ding.pk.service")
public class SchedulingTestConfig {

    @Value("${cache.max.amount.items:5000}") private Integer capacity;

    @Bean
    public InMemory3DCache<String, String, String> inMemoryCache() {
        return new PingInMemory3DCache<>(capacity);
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

//    @Bean
//    public SapMaterialService materialService() {
//        return mock(SapMaterialService.class);
//    }

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

    @Bean
    public SalesOfficeService salesOfficeService() {
        return mock(SalesOfficeService.class);
    }

    @Bean
    public PriceRowService priceRowService() {
        return mock(PriceRowService.class);
    }

    @Bean
    public SapHttpClient sapHttpClient() {
        return mock(SapHttpClient.class);
    }

    @Bean
    public InMemory3DCache<String, String, MaterialStdPriceDTO> standardPriceInMemoryCache() {
        return mock(InMemory3DCache.class);
    }

    @Bean
    public InMemory3DCache<String, String, MaterialDTO> materialInMemoryCache() {
        return mock(InMemory3DCache.class);
    }
}
