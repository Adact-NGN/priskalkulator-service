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

    @Value("${CLIENT_ID}")
    private String clientId;
    @Value("${AUTHORITY}")
    private String authority;
    @Value("${SECRET}")
    private String secret;
    @Value("${SCOPE}")
    private String scope;

    @Bean
    public ConfidentialClientApplication confidentialClientApplication() throws MalformedURLException {
        log.debug("Building ConfidentialClientApplication with client id: " + clientId);
        return ConfidentialClientApplication.builder(clientId,
                        ClientCredentialFactory.createFromSecret(secret))
                .authority(authority)
                .build();
    }
}
