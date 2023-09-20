package no.ding.pk.service;

import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesOfficePowerOfAttorneyRepository;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Disabled
class SalesOfficePowerOfAttorneyServiceImplTest extends AbstractIntegrationConfig {

    @Autowired
    private SalesOfficePowerOfAttorneyRepository sopaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesRoleRepository salesRoleRepository;

    private SalesOfficePowerOfAttorneyService service;

    private UserService userService;


    @BeforeEach
    public void setup() {
        service = new SalesOfficePowerOfAttorneyServiceImpl(sopaRepository);

        userService = new UserServiceImpl(userRepository, salesRoleRepository);

        sopaRepository.deleteAll();
    }

    @Test
    public void shouldPersistPowerOfAttorney() {
        PowerOfAttorney poa = sopaRepository.findBySalesOffice(101);

        if(poa == null) {
            poa = PowerOfAttorney.builder()
                    .salesOffice(101)
                    .salesOfficeName("StorOslo")
                    .region("Oslofjord")
                    .build();
        }

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

        PowerOfAttorney poa = sopaRepository.findBySalesOffice(100);

        if(poa == null) {
            poa = PowerOfAttorney.builder()
                    .salesOffice(100)
                    .salesOfficeName("StorOslo")
                    .region("Oslofjord")
                    .ordinaryWasteLvlOneHolder(user)
                    .ordinaryWasteLvlTwoHolder(user)
                    .dangerousWasteHolder(user)
                    .build();
        } else {
            poa.setOrdinaryWasteLvlOneHolder(user);
            poa.setOrdinaryWasteLvlTwoHolder(user);
            poa.setDangerousWasteHolder(user);
        }

        poa = service.save(poa);

        assertThat(poa.getId(), notNullValue());
        assertThat(poa.getOrdinaryWasteLvlOneHolder(), notNullValue());
        assertThat(poa.getOrdinaryWasteLvlTwoHolder(), notNullValue());
        assertThat(poa.getDangerousWasteHolder(), notNullValue());
    }
}