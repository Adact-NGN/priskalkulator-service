package no.ding.pk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceTest {
    
    @Autowired
    private UserService service;
    
    @Autowired
    private SalesRoleService salesRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;
    
    private List<User> testData;
    
    @BeforeEach
    public void setup() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(Objects.requireNonNull(classLoader.getResource("user.json")).getFile());
        
        assertThat(file.exists(), is(true));
        
        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
        
        assertThat("JSON is empty", json, not(emptyOrNullString()));
        
        testData = new ArrayList<>();
        
        JSONArray results = new JSONArray(json);

        for(int i = 0; i < results.length(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);
            
            try {
                UserDTO discount = objectMapper.readValue(jsonObject.toString(), UserDTO.class);
                User user = modelMapper.map(discount, User.class);
                testData.add(user);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        assertThat("Test data is empty, reading in JSON file failed.", testData, not(empty()));

        SalesRole kv = salesRoleService.findSalesRoleByRoleName("KV");

        if(kv == null) {
            kv = SalesRole.builder()
                    .roleName("KV")
                    .description("Kundeveileder")
                    .defaultPowerOfAttorneyOa(1)
                    .defaultPowerOfAttorneyFa(1)
                    .build();

            salesRoleService.save(kv);
        }

        SalesRole kv2 = salesRoleService.findSalesRoleByRoleName("SA");

        if(kv2 == null) {
            kv2 = SalesRole.builder()
                    .defaultPowerOfAttorneyFa(2)
                    .defaultPowerOfAttorneyOa(2)
                    .description("Salgskonsulent (rolle a)")
                    .roleName("SA")
                    .build();

            salesRoleService.save(kv2);
        }
    }
    
    @Test
    public void shouldPersistUserWithSalesRole() {
        SalesRole salesRole = salesRoleService.findSalesRoleByRoleName("KV");
        
        if(salesRole == null) {
            salesRole = testData.get(0).getSalesRole();
            
            salesRole = salesRoleService.save(salesRole);
        }
        
        User userObject = service.findByEmail(testData.get(0).getEmail());

        if(userObject == null) {
            userObject = testData.get(0);
        }
        userObject.setSalesRole(salesRole);
        
        userObject = service.save(userObject, userObject.getId());
        
        Optional<User> actual = service.findById(userObject.getId());
        
        assertThat(actual.isPresent(), is(true));
        
        User actualUser = actual.get();
        
        SalesRole salesRoleEntity = salesRoleService.findSalesRoleByRoleName(actualUser.getSalesRole().getRoleName());
        
        assertThat(salesRoleEntity, is(notNullValue()));
        
        assertThat(salesRoleEntity.getUserList().size(), greaterThan(0));
    }
    
    @Test
    public void shouldRemoveSalesRoleFromUser() {
        SalesRole salesRole = salesRoleService.findSalesRoleByRoleName("KV");
        
        if(salesRole == null) {
            salesRole = SalesRole.builder()
            .roleName("KV")
            .description("Kundeveileder")
            .defaultPowerOfAttorneyFa(1)
            .defaultPowerOfAttorneyOa(1)
            .build();
            
            salesRole = salesRoleService.save(salesRole);
        }

        User userObject = service.findByEmail(testData.get(0).getEmail());

        if(userObject == null) {
            userObject = testData.get(0);
            userObject.setSalesRole(null);
            userObject = service.save(userObject, null);
        }

        salesRole.addUser(userObject);

        salesRole = salesRoleService.save(salesRole);

        userObject = service.findById(userObject.getId()).get();

        assertThat(userObject.getSalesRole(), equalTo(salesRole));

        salesRole.removeUser(userObject);

        salesRole = salesRoleService.save(salesRole);

        userObject = service.save(userObject, userObject.getId());
        
        assertThat(userObject.getSalesRole(), nullValue());
        
        List<SalesRole> salesRoleList = salesRoleService.getAllSalesRoles();
        
        assertThat(salesRoleList, hasSize(greaterThan(0)));
        
        SalesRole actualSalesRole = salesRoleList.get(0);
        
        assertThat(actualSalesRole.getUserList(), hasSize(0));
    }
    
    @Test
    public void shouldChangeSalesRoleForUser() {
        SalesRole firstSalesRole = salesRoleService.findSalesRoleByRoleName("KV");
        
        if(firstSalesRole == null) {
            firstSalesRole = SalesRole.builder()
            .roleName("KV")
            .description("Kundeveileder")
            .defaultPowerOfAttorneyFa(1)
            .defaultPowerOfAttorneyOa(1)
            .build();
            
            firstSalesRole = salesRoleService.save(firstSalesRole);
        }
        
        SalesRole otherSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        
        if(otherSalesRole == null) {
            otherSalesRole = SalesRole.builder()
            .roleName("SA")
            .description("Salgskonsulent (rolle a)")
            .defaultPowerOfAttorneyFa(2)
            .defaultPowerOfAttorneyOa(2)
            .build();
            
            otherSalesRole = salesRoleService.save(otherSalesRole);
        }
        
        User userObject = service.findByEmail(testData.get(0).getEmail());

        if(userObject == null) {
            userObject = testData.get(0);
        }
        userObject.setSalesRole(firstSalesRole);
        
        userObject.setPowerOfAttorneyFA(1);
        userObject.setPowerOfAttorneyOA(1);
        
        userObject = service.save(userObject, null);
        
        Optional<User> optUser = service.findById(userObject.getId());
        
        assertThat(optUser.isPresent(), is(true));
        
        User actualUser = optUser.get();
        
        assertThat(actualUser.getSalesRole(), equalTo(firstSalesRole));
        assertThat(actualUser.getPowerOfAttorneyFA(), equalTo(1));
        assertThat(actualUser.getPowerOfAttorneyFA(), equalTo(1));
        
        User newUserObject = SerializationUtils.clone(actualUser);
        newUserObject.setId(userObject.getId());
        
        newUserObject.setSalesRole(otherSalesRole);
        newUserObject.setPowerOfAttorneyFA(2);
        newUserObject.setPowerOfAttorneyOA(2);
        
        newUserObject.setSureName("Minde");
        
        actualUser = service.save(newUserObject, newUserObject.getId());
        
        optUser = service.findById(userObject.getId());
        
        assertThat(optUser.isPresent(), is(true));
        
        actualUser = optUser.get();
        
        assertThat(actualUser.getPowerOfAttorneyFA(), equalTo(2));
        assertThat(actualUser.getPowerOfAttorneyOA(), equalTo(2));
        assertThat(actualUser.getSureName(), equalTo("Minde"));
        
        List<SalesRole> salesRoleList = salesRoleService.getAllSalesRoles();
        
        assertThat(salesRoleList, hasSize(greaterThan(0)));
        
        firstSalesRole = salesRoleService.findSalesRoleByRoleName(firstSalesRole.getRoleName());
        
        assertThat(firstSalesRole.getUserList(), hasSize(0));
        
        otherSalesRole = salesRoleService.findSalesRoleByRoleName(otherSalesRole.getRoleName());
        
        assertThat(otherSalesRole.getUserList(), hasSize(1));
    }
}
