package no.ding.pk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.repository.offer.MaterialPriceRepository;
import no.ding.pk.repository.offer.MaterialRepository;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.offer.MaterialPriceService;
import no.ding.pk.service.offer.MaterialPriceServiceImpl;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.service.offer.MaterialServiceImpl;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@Disabled
public class UserServiceTest extends AbstractIntegrationConfig {

    private MaterialService materialService;

    private UserService service;
    
    private SalesRoleService salesRoleService;

    private ObjectMapper objectMapper;

    private ModelMapper modelMapper;
    
    private List<User> testData;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesRoleRepository salesRoleRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private MaterialPriceService materialPriceService;

    @Autowired
    private MaterialPriceRepository materialPriceRepository;

    @BeforeEach
    public void setup() throws IOException {

        service = new UserServiceImpl(userRepository, salesRoleRepository);

        salesRoleService = new SalesRoleServiceImpl(salesRoleRepository);

        materialPriceService = new MaterialPriceServiceImpl(materialPriceRepository);

        objectMapper = new ObjectMapper();

        ModelMapperV2Config modelMapperV2Config = new ModelMapperV2Config();

        materialService = new MaterialServiceImpl(materialRepository, materialPriceService);
        modelMapper = modelMapperV2Config.modelMapperV2(materialService, salesRoleRepository);

        deleteAllUsers();

        deleteAllUserRoles();

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
            kv = SalesRole.builder("KV", 1, 1)
                    .description("Kundeveileder")
                    .build();

            salesRoleService.save(kv);
        }

        SalesRole kv2 = salesRoleService.findSalesRoleByRoleName("SA");

        if(kv2 == null) {
            kv2 = SalesRole.builder("SA", 2, 2)
                    .description("Salgskonsulent (rolle a)")
                    .build();

            salesRoleService.save(kv2);
        }
    }

    private void deleteAllUserRoles() {
        try {
            salesRoleRepository.deleteAll();
        } catch (InvalidDataAccessResourceUsageException e) {
            return;
        }
    }

    private void deleteAllUsers() {
        try {
            userRepository.deleteAll();
        } catch (InvalidDataAccessResourceUsageException e) {
            return;
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

        userObject = service.updateSalesRoleForUser(userObject, salesRole);

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
            salesRole = SalesRole.builder("KV", 1, 1)
            .description("Kundeveileder")
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
            firstSalesRole = SalesRole.builder("KV", 1, 1)
            .description("Kundeveileder")
            .build();
            
            firstSalesRole = salesRoleService.save(firstSalesRole);
        }
        
        SalesRole otherSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        
        if(otherSalesRole == null) {
            otherSalesRole = SalesRole.builder("SA", 2, 2)
            .description("Salgskonsulent (rolle a)")
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

        newUserObject.setPowerOfAttorneyFA(2);
        newUserObject.setPowerOfAttorneyOA(2);
        service.updateSalesRoleForUser(newUserObject, otherSalesRole);

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
