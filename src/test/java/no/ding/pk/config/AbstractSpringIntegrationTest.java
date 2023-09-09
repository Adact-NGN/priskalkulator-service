package no.ding.pk.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

@SpringBootTest
@ActiveProfiles("integrationTest")
@AutoConfigureMockMvc
public abstract class AbstractSpringIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    private List<String> tableNames = List.of()

    @BeforeEach
    public void setup() {
        cleanAllDatabases();
    }

    private void cleanAllDatabases() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate)
    }
}
