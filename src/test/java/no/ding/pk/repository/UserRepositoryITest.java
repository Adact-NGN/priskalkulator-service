package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@Profile("itest")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/azure-db.properties")
public class UserRepositoryITest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository repository;


    @Test
    public void shouldConnectToTheRemoteDatabase() {
        assertThat(true, equalTo(true));
    }
    
}
