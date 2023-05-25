package no.ding.pk.repository;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SalesRoleRepositoryTest {
    
    @Autowired
    private SalesRoleRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldPersistSalesRole() {
        SalesRole expected = repository.findByRoleName("KV");

        if(expected == null) {
            expected = new SalesRole();
            expected.setDefaultPowerOfAttorneyFa(1);
            expected.setDefaultPowerOfAttorneyOa(1);
            expected.setDescription("Kundeveileder");
            expected.setRoleName("KV");

            repository.save(expected);
        }

        List<SalesRole> result = repository.findAll();

        assertThat(result, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldPersistUserWithSalesRole() {
        SalesRole persistedSalesRole = createSalesRoleWithUser();

        SalesRole actual = repository.findById(persistedSalesRole.getId()).orElse(null);

        assertThat(actual, notNullValue());
        assertThat(actual.getUserList(), hasSize(1));
    }

    private SalesRole createSalesRoleWithUser() {
        SalesRole salesRole = repository.findByRoleName("KV");

        if(salesRole == null) {
            salesRole = new SalesRole();

            salesRole.setDefaultPowerOfAttorneyFa(1);
            salesRole.setDefaultPowerOfAttorneyOa(1);
            salesRole.setDescription("Kundeveileder");
            salesRole.setRoleName("KV");

            salesRole = repository.save(salesRole);
        }

        User user = new User();
        user.setAdId("dc804853-6a82-4022-8eb5-244fff724af2");
        user.setAssociatedPlace("Larvik");
        user.setEmail("kjetil.torvund.minde@ngn.no");
        user.setFullName("Kjetil Torvund Minde");
        user.setName("Kjetil");
        user.setResourceNr("63940");

        user = userRepository.save(user);

        salesRole.addUser(user);

        return repository.save(salesRole);
    }
}
