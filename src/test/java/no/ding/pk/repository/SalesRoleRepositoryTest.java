package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SalesRoleRepositoryTest {
    
    @Autowired
    private SalesRoleRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    public void shouldPersistSalesRole() {
        SalesRole expected = new SalesRole();
        expected.setDefaultPowerOfAttorneyFa(1);
        expected.setDefaultPowerOfAttorneyOa(1);
        expected.setDescription("Kundeveileder");
        expected.setRoleName("KV");

        repository.save(expected);

        List<SalesRole> result = repository.findAll();

        assertThat(result, hasSize(greaterThan(0)));
    }

    @Transactional
    @Test
    public void shouldPersistUserWithSalesRole() {
        SalesRole persistedSalesRole = createSalesRoleWithUser();

        SalesRole actual = repository.findByIdWithUserList(persistedSalesRole.getId());

        assertThat(actual.getUserList(), hasSize(1));
    }

    @Transactional
    private SalesRole createSalesRoleWithUser() {
        SalesRole salesRole = new SalesRole();
        salesRole.setDefaultPowerOfAttorneyFa(1);
        salesRole.setDefaultPowerOfAttorneyOa(1);
        salesRole.setDescription("Kundeveileder");
        salesRole.setRoleName("KV");

        salesRole = repository.save(salesRole);

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
