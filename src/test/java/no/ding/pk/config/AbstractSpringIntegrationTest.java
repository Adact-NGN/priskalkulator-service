package no.ding.pk.config;

import org.junit.Rule;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integrationTest")
@AutoConfigureMockMvc
public abstract class AbstractSpringIntegrationTest {
}
