package no.ding.pk.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;

@SpringBootTest
@TestPropertySource("/h2-db.properties")
public class UserServiceTest {
    
    @Autowired
    private UserService service;
    
    @Autowired
    private SalesRoleService salesRoleService;
    
    private List<User> testData;
    
    @BeforeEach
    public void setup() throws FileNotFoundException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(classLoader.getResource("user.json").getFile());
        
        assertThat(file.exists(), is(true));
        
        String json = IOUtils.toString(new FileInputStream(file), "UTF-8");
        
        assertThat("JSON is empty", json, not(emptyOrNullString()));
        
        testData = new ArrayList<>();
        
        JSONArray results = new JSONArray(json);
        
        ObjectMapper om = new ObjectMapper();
        
        for(int i = 0; i < results.length(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);
            
            try {
                User discount = om.readValue(jsonObject.toString(), User.class);
                testData.add(discount);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        assertThat("Test data is empty, reading in JSON file failed.", testData, not(empty()));
        
    }
    
    // @Transactional
    @Test
    public void shouldPersistUserWithSalesRole() {
        SalesRole salesRole = salesRoleService.findSalesRoleByRoleName(testData.get(0).getSalesRole().getRoleName());
        
        if(salesRole == null) {
            salesRole = testData.get(0).getSalesRole();
            
            salesRole = salesRoleService.save(salesRole);
        }
        
        User userObject = testData.get(0);
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
        SalesRole salesRole = SalesRole.builder()
        .roleName("KV")
        .description("Kundeveileder")
        .defaultPowerOfAttorneyFa(1)
        .defaultPowerOfAttorneyOa(1)
        .build();
        
        salesRole = salesRoleService.save(salesRole);
        
        User userObject = testData.get(0);
        userObject.setSalesRole(salesRole);
        
        userObject = service.save(userObject, userObject.getId());
        
        Optional<User> optUser = service.findById(userObject.getId());
        
        assertThat(optUser.isPresent(), is(true));
        
        User actualUser = optUser.get();
        
        assertThat(actualUser.getSalesRole(), notNullValue());
        
        actualUser.setSalesRole(null);
        
        actualUser = service.save(actualUser, userObject.getId());
        
        assertThat(actualUser.getSalesRole(), nullValue());
        
        List<SalesRole> salesRoleList = salesRoleService.getAllSalesRoles();
        
        assertThat(salesRoleList, hasSize(greaterThan(0)));
        
        SalesRole actualSalesRole = salesRoleList.get(0);
        
        assertThat(actualSalesRole.getUserList(), hasSize(0));
    }
    
    @Test
    public void shouldChangeSalesRoleForUser() {
        SalesRole firstSalesRole = SalesRole.builder()
        .roleName("KV")
        .description("Kundeveileder")
        .defaultPowerOfAttorneyFa(1)
        .defaultPowerOfAttorneyOa(1)
        .build();
        
        firstSalesRole = salesRoleService.save(firstSalesRole);
        
        SalesRole otherSalesRole = SalesRole.builder()
        .roleName("SA")
        .description("Salgskonsulent (rolle a)")
        .defaultPowerOfAttorneyFa(2)
        .defaultPowerOfAttorneyOa(2)
        .build();
        
        otherSalesRole = salesRoleService.save(otherSalesRole);
        
        User userObject = testData.get(0);
        userObject.setSalesRole(firstSalesRole);
        
        userObject.setPowerOfAtterneyFA(1);
        userObject.setPowerOfAtterneyOA(1);
        
        userObject = service.save(userObject, null);
        
        Optional<User> optUser = service.findById(userObject.getId());
        
        assertThat(optUser.isPresent(), is(true));
        
        User actualUser = optUser.get();
        
        assertThat(actualUser.getSalesRole(), equalTo(firstSalesRole));
        assertThat(actualUser.getPowerOfAtterneyFA(), equalTo(1));
        assertThat(actualUser.getPowerOfAtterneyFA(), equalTo(1));
        
        actualUser.setSalesRole(otherSalesRole);
        actualUser.setPowerOfAtterneyFA(2);
        actualUser.setPowerOfAtterneyOA(2);
        
        actualUser.setSureName("Minde");
        
        actualUser = service.save(actualUser, actualUser.getId());
        
        optUser = service.findById(userObject.getId());
        
        assertThat(optUser.isPresent(), is(true));
        
        actualUser = optUser.get();
        
        assertThat(actualUser.getPowerOfAtterneyFA(), equalTo(2));
        assertThat(actualUser.getPowerOfAtterneyOA(), equalTo(2));
        assertThat(actualUser.getSureName(), equalTo("Minde"));
        
        List<SalesRole> salesRoleList = salesRoleService.getAllSalesRoles();
        
        assertThat(salesRoleList, hasSize(greaterThan(0)));
        
        firstSalesRole = salesRoleService.findSalesRoleByRoleName(firstSalesRole.getRoleName());
        
        assertThat(firstSalesRole.getUserList(), hasSize(0));
        
        otherSalesRole = salesRoleService.findSalesRoleByRoleName(otherSalesRole.getRoleName());
        
        assertThat(otherSalesRole.getUserList(), hasSize(1));
    }
}
