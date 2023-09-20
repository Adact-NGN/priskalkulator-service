package no.ding.pk.config;

import no.ding.pk.service.sap.SapMaterialService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class SapMaterialServiceConfig {
    @Bean
    public SapMaterialService sapMaterialService() {
        return mock(SapMaterialService.class);
    }
}
