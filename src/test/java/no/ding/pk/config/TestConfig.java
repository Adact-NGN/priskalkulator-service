package no.ding.pk.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

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
}
