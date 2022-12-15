package no.ding.pk.config;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@DataJpaTest
@TestPropertySource("/h2-db.properties")
public class H2TestConfig {
    
}
