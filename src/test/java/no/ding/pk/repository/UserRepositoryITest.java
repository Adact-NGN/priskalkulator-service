package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles({"itest"})
@SpringBootTest
@TestPropertySource("/application.properties")
public class UserRepositoryITest {

    @Autowired
    private UserRepository repository;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.url}")
    private String dbUrl;


    @Test
    public void shouldConnectToTheRemoteDatabase() {
        assertThat(repository, notNullValue());
    }

}
