package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import no.ding.pk.domain.User;

@Ignore
@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("/h2-db.properties")
public class H2UserRepositoryTest {
    
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
