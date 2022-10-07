package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import no.ding.pk.domain.User;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application.properties")
public class UserRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test
    @Transactional
    public void testFindBySureName() {
        User user = new User();

        user.setName("Test");
        user.setSureName("Testesen");

        entityManager.persist(user);

        List<User> actual = repository.findBySureName("Testesen");

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).getSureName(), equalTo("Testesen"));
    }
}
