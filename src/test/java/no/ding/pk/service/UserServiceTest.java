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
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;

@Transactional
@SpringBootTest
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
@TestPropertySource("/h2-db.properties")
public class UserServiceTest {
    
    @Autowired
    private UserService service;
    
    @Autowired
    private SalesRoleRepository salesRoleRepository;
    
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
    
    @Test
    public void shouldPersistUserWithSalesRole() {
        SalesRole salesRole = salesRoleRepository.findByRoleName(testData.get(0).getSalesRole().getRoleName());
        
        if(salesRole == null) {
            salesRole = testData.get(0).getSalesRole();
            
            salesRole = salesRoleRepository.save(salesRole);
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
        
        SalesRole salesRoleEntity = salesRoleRepository.findByRoleName(actualUser.getSalesRole().getRoleName());
        
        assertThat(salesRoleEntity, is(notNullValue()));
        
        assertThat(salesRoleEntity.getUserList().size(), greaterThan(0));
    }
    
    @Test
    public void shouldRemoveSalesRoleFromUser() {
        SalesRole salesRole = salesRoleRepository.findByRoleName("KV");
        
        if(salesRole == null) {
            salesRole = SalesRole.builder()
            .roleName("KV")
            .description("Kundeveileder")
            .defaultPowerOfAttorneyFa(1)
            .defaultPowerOfAttorneyOa(1)
            .build();
            
            salesRole = salesRoleRepository.save(salesRole);
        }
        
        User userObject = service.findByEmail(testData.get(0).getEmail());

        if(userObject == null) {
            userObject = testData.get(0);
        }

        userObject.setSalesRole(salesRole);

        userObject = service.save(userObject, userObject.getId());
        
        assertThat(userObject.getSalesRole(), notNullValue());

        User deepCopy = SerializationUtils.clone(userObject);
        
        deepCopy.setSalesRole(null);
        
        userObject = service.save(deepCopy, deepCopy.getId());
        
        assertThat(userObject.getSalesRole(), nullValue());
        
        List<SalesRole> salesRoleList = salesRoleRepository.findAll();
        
        assertThat(salesRoleList, hasSize(greaterThan(0)));
        
        SalesRole actualSalesRole = salesRoleList.get(0);
        
        assertThat(actualSalesRole.getUserList(), hasSize(0));
    }
    
    @Test
    public void shouldChangeSalesRoleForUser() {
        SalesRole firstSalesRole = salesRoleRepository.findByRoleName("KV");
        
        if(firstSalesRole == null) {
            firstSalesRole = SalesRole.builder()
            .roleName("KV")
            .description("Kundeveileder")
            .defaultPowerOfAttorneyFa(1)
            .defaultPowerOfAttorneyOa(1)
            .build();
            
            firstSalesRole = salesRoleRepository.save(firstSalesRole);
        }
        
        SalesRole otherSalesRole = salesRoleRepository.findByRoleName("SA");
        
        if(otherSalesRole == null) {
            otherSalesRole = SalesRole.builder()
            .roleName("SA")
            .description("Salgskonsulent (rolle a)")
            .defaultPowerOfAttorneyFa(2)
            .defaultPowerOfAttorneyOa(2)
            .build();
            
            otherSalesRole = salesRoleRepository.save(otherSalesRole);
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
        
        List<SalesRole> salesRoleList = salesRoleRepository.findAll();
        
        assertThat(salesRoleList, hasSize(greaterThan(0)));
        
        firstSalesRole = salesRoleRepository.findByRoleName(firstSalesRole.getRoleName());
        
        assertThat(firstSalesRole.getUserList(), hasSize(0));
        
        otherSalesRole = salesRoleRepository.findByRoleName(otherSalesRole.getRoleName());
        
        assertThat(otherSalesRole.getUserList(), hasSize(1));
    }
}
