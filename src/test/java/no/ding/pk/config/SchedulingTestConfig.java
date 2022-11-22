package no.ding.pk.config;

import static org.mockito.Mockito.mock;

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
import no.ding.pk.repository.UserRepository;
import no.ding.pk.service.MaterialInMemoryCache;
import no.ding.pk.web.mappers.MapperService;

@Configuration
@EnableScheduling
@Profile({ "unit-test" })
@ComponentScan("no.ding.pk.service")
public class SchedulingTestConfig {
    @Bean
    public MaterialInMemoryCache<String, String, String> inMemoryCache() {
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
}
