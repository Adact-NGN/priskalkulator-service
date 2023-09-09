package no.ding.pk.service;

import no.ding.pk.config.AbstractSpringIntegrationTest;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

//@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
//@Sql(value = { "/power_of_attorney/drop_schema.sql", "/power_of_attorney/create_schema.sql"})
class SalesOfficePowerOfAttorneyServiceImplTest extends AbstractSpringIntegrationTest {
//    https://auto1.tech/integration-test-speedup/
//    https://www.baeldung.com/spring-tests

    @Autowired
    private SalesOfficePowerOfAttorneyService service;

    @Autowired
    private UserService userService;

    @Test
    public void shouldPersistPowerOfAttorney() {
        PowerOfAttorney poa = PowerOfAttorney.builder()
                .salesOffice(101)
                .salesOfficeName("StorOslo")
                .region("Oslofjord")
                .build();

        poa = service.save(poa);

        assertThat(poa.getId(), notNullValue());
    }

    @Test
    public void shouldUpdatePowerOfAttorneyWithUsers() {
        User user = User.builder()
                .adId("dc804853-6a82-4022-8eb5-244fff724af2")
                .associatedPlace("Larvik")
                .email("kjetil.torvund.minde@ngn.no")
                .fullName("Kjetil Torvund Minde")
                .name("Kjetil")
                .powerOfAttorneyOA(5)
                .powerOfAttorneyFA(5)
                .build();

        user = userService.save(user, null);

        PowerOfAttorney poa = PowerOfAttorney.builder()
                .salesOffice(100)
                .salesOfficeName("StorOslo")
                .region("Oslofjord")
                .ordinaryWasteLvlOneHolder(user)
                .ordinaryWasteLvlTwoHolder(user)
                .dangerousWasteHolder(user)
                .build();

        poa = service.save(poa);

        assertThat(poa.getId(), notNullValue());
        assertThat(poa.getOrdinaryWasteLvlOneHolder(), notNullValue());
        assertThat(poa.getOrdinaryWasteLvlTwoHolder(), notNullValue());
        assertThat(poa.getDangerousWasteHolder(), notNullValue());
    }
}