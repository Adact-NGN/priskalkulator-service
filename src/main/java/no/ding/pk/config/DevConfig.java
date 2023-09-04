package no.ding.pk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile({"dev"})
@Configuration
@PropertySource({
        "classpath:application-dev.properties",
})
public class DevConfig {
    
}
