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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import no.ding.pk.domain.User;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    @Transactional
    public void testFindBySureName() {
        User user = new User();

        user.setName("Test");
        user.setSureName("Testesen");

        repository.save(user);

        List<User> actual = repository.findBySureName("Testesen");

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).getSureName(), equalTo("Testesen"));
    }
}
