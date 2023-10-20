package no.ding.pk.service;

import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Disabled
class SalesRoleServiceImplTest extends AbstractIntegrationConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesRoleRepository salesRoleRepository;

    private UserService userService;
    
    private SalesRoleService salesRoleService;

    @BeforeEach
    public void setup() {
        userService  = new UserServiceImpl(userRepository, salesRoleRepository);

        salesRoleService = new SalesRoleServiceImpl(salesRoleRepository);
    }

    @AfterEach
    public void tearDown() {
        salesRoleRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    public void shouldPersistUserWithSalesRole() {
        SalesRole saSalesRole = createSaSalesRole();
        
        saSalesRole = salesRoleService.save(saSalesRole);
        
        User saUser = createSaUser();
        
        saUser = userService.save(saUser, null);
        
        saSalesRole.addUser(saUser);
        
        saSalesRole = salesRoleService.save(saSalesRole);
        
        assertThat(saSalesRole.getUserList(), hasSize(greaterThan(0)));
    }
    
    @Test
    public void shouldChangeUsersSalesRole() {
        SalesRole saSalesRole = createSaSalesRole();
        
        saSalesRole = salesRoleService.save(saSalesRole);
        
        User user = createSaUser();
        
        user = userService.save(user, null);

        user = userService.updateSalesRoleForUser(user, saSalesRole);

        assertThat(user.getSalesRole(), notNullValue());
        
        SalesRole knSalesRole = createKnSalesRole();

        knSalesRole = salesRoleService.save(knSalesRole);

        user = userService.updateSalesRoleForUser(user, knSalesRole);

        assertThat(user.getSalesRole(), equalTo(knSalesRole));

        saSalesRole = salesRoleService.findSalesRoleByRoleName("SA");

        assertThat(saSalesRole.getUserList(), hasSize(0));
    }
    
    @Test
    public void shouldAddMultipleUsersToSalesRole() {
        SalesRole saSalesRole = createSaSalesRole();
        
        saSalesRole = salesRoleService.save(saSalesRole);
        
        User user_1 = createSaUser();
        
        user_1 = userService.save(user_1, null);
        
        User user_2 = createKnUser();
        
        user_2 = userService.save(user_2, null);
        
        saSalesRole.addUser(user_1);
        saSalesRole.addUser(user_2);
        
        saSalesRole = salesRoleService.save(saSalesRole);
        
        assertThat(saSalesRole.getUserList(), hasSize(2));
    }
    
    private SalesRole createSaSalesRole() {
        SalesRole sa = salesRoleRepository.findByRoleName("SA");

        if(sa != null) {
            return sa;
        }
        return SalesRole.builder("SA", 2, 2)
        .description("Salgskonsulent (rolle a)")
        .build();
    }
    
    private SalesRole createKnSalesRole() {
        SalesRole kn = salesRoleRepository.findByRoleName("KN");

        if(kn != null) {
            return kn;
        }

        return SalesRole.builder("KN", 5, 5)
        .description("KAM nasjonalt")
        .build();
    }
    
    private User createSaUser() {
        return User.builder("Wolfgang Amadeus", "Mozart", "Wolfgang Amadeus Mozart", "Wolfgang@farris-bad.no", "Wolfgang@farris-bad.no")
        .adId("ad-id-wegarijo-arha-rh-arha")
        .jobTitle("Komponist")
        .associatedPlace("Larvik")
        .department("Hvitsnippene")
        .build();
    }
    
    private User createKnUser() {
        return User.builder("Alexander", "Brox", "Alexander Brox", "alexander.brox@ngn.no", "alexander.brox@ngn.no")
        .adId("e2f1963a-072a-4414-8a0b-6a3aa6988e0c")
        .orgNr("100")
        .resourceNr("63874")
        .associatedPlace("Oslo")
        .phoneNumber("95838638")
        .jobTitle("Markedskonsulent")
        .powerOfAttorneyOA(5)
        .powerOfAttorneyFA(3)
        .build();
    }
    
}