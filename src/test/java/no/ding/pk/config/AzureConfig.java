package no.ding.pk.config;

import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class AzureConfig {

    @Bean
    public ConfidentialClientApplication confidentialClientApplication() {
        return mock(ConfidentialClientApplication.class);
    }
}
