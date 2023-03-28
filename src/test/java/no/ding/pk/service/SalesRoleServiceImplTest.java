package no.ding.pk.service;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SalesRoleServiceImplTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SalesRoleService salesRoleService;
    
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
        
        User saUser = createSaUser();
        
        saUser = userService.save(saUser, null);
        
        saSalesRole.addUser(saUser);
        
        saSalesRole = salesRoleService.save(saSalesRole);
        
        SalesRole knSalesRole = createKnSalesRole();
        
        knSalesRole = salesRoleService.save(knSalesRole);
        
        knSalesRole.addUser(saUser);
        
        knSalesRole = salesRoleService.save(knSalesRole);
        
        saSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        
        assertThat(knSalesRole.getUserList(), hasSize(greaterThan(0)));
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
        return SalesRole.builder()
        .roleName("SA")
        .description("Salgskonsulent (rolle a)")
        .defaultPowerOfAttorneyOa(2)
        .defaultPowerOfAttorneyFa(2)
        .build();
    }
    
    private SalesRole createKnSalesRole() {
        return SalesRole.builder()
        .roleName("KN")
        .description("KAM nasjonalt")
        .defaultPowerOfAttorneyOa(5)
        .defaultPowerOfAttorneyFa(5)
        .build();
    }
    
    private User createSaUser() {
        return User.builder()
        .adId("ad-id-wegarijo-arha-rh-arha")
        .jobTitle("Komponist")
        .fullName("Wolfgang Amadeus Mozart")
        .email("Wolfgang@farris-bad.no")
        .associatedPlace("Larvik")
        .department("Hvitsnippene")
        .build();
    }
    
    private User createKnUser() {
        return User.builder()
        .adId("e2f1963a-072a-4414-8a0b-6a3aa6988e0c")
        .name("Alexander")
        .sureName("Brox")
        .fullName("Alexander Brox")
        .orgNr("100")
        .resourceNr("63874")
        .associatedPlace("Oslo")
        .phoneNumber("95838638")
        .email("alexander.brox@ngn.no")
        .jobTitle("Markedskonsulent")
        .powerOfAttorneyOA(5)
        .powerOfAttorneyFA(3)
        .build();
    }
    
}