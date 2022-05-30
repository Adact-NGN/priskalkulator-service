package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import no.ding.pk.domain.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test
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
