package no.ding.pk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile({"prod"})
@Configuration
@PropertySource({"classpath:db.properties"})
public class ProdConfig {
    
}
