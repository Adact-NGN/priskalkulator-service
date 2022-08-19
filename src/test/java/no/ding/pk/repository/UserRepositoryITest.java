package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import no.ding.pk.domain.User;

@Ignore
@Profile("itest")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:azure-db.properties")
public class UserRepositoryITest {

    @Autowired
    private UserRepository repository;

    @Value("spring.datasource.username")
    private String username;


    @Test
    public void shouldConnectToTheRemoteDatabase() {
        List<User> userList = repository.findAll();

        assertThat(userList, not(empty()));
    }
    
}
