package no.ding.pk.config;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@Tag("integrationtest")
@ContextConfiguration
@Import(H2TestConfig.class)
@DataJpaTest
public abstract class AbstractIntegrationConfig {
}
