package no.ding.pk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile({"dev", "default"})
@Configuration
@PropertySource({
    "classpath:db-dev.properties", 
})
public class DevConfig {
    
}
