package no.ding.pk.config.azure;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
public class AzureConfig {
    private final Logger log = LoggerFactory.getLogger(AzureConfig.class);

    @Bean
    public ConfidentialClientApplication confidentialClientApplication(
            @Value("${CLIENT_ID}") String clientId,
            @Value("${AUTHORITY}") String authority,
            @Value("${SECRET}") String secret,
            @Value("${SCOPE}") String scope
            ) throws MalformedURLException {
        log.debug("Building ConfidentialClientApplication with client id: " + clientId);
        return ConfidentialClientApplication.builder(clientId,
                        ClientCredentialFactory.createFromSecret(secret))
                .authority(authority)
                .build();
    }
}
