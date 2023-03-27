package no.ding.pk.config;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import no.ding.pk.web.mappers.MapperService;

@TestConfiguration
public class TestConfig {
    @Bean
    public DataSource DataSource() {
        return Mockito.mock(DataSource.class);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Mockito.mock(EntityManagerFactory.class);
    }

    @Bean 
    public MapperService mapperService() {
        return Mockito.mock(MapperService.class);
    }
}
