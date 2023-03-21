package no.ding.pk.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
public class UserControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @Autowired
    private SalesRoleService salesRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesRoleRepository salesRoleRepository;

    private List<UserDTO> testData;
    private String requestPayload;

    @BeforeEach
    public void setup() throws IOException {
//        initializeDiscounts(discountService, objectMapper);
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(Objects.requireNonNull(classLoader.getResource("user.json")).getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
        requestPayload = json;

        assertThat("JSON is empty", json, not(emptyOrNullString()));

        testData = new ArrayList<>();

        JSONArray results = new JSONArray(json);

        for(int i = 0; i < results.length(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);

            try {
                UserDTO discount = objectMapper.readValue(jsonObject.toString(), UserDTO.class);
                testData.add(discount);
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
    public void shouldPersistUserWithSalesRole() throws JsonProcessingException {
        SalesRole salesRole = salesRoleService.getAllSalesRoles().get(0);

        JSONArray jsonArray = new JSONArray(requestPayload);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        jsonObject.put("salesRoleId", salesRole.getId());
        UserDTO userDTO = objectMapper.readValue(jsonObject.toString(), UserDTO.class);

        assertThat(userDTO, notNullValue());
        assertThat(userDTO.getSalesRoleId(), notNullValue());

        ResponseEntity<UserDTO> actual = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/users/create", userDTO, UserDTO.class); // userController.create(userDTO);

        assertThat(actual.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(actual.getBody(), notNullValue());

        UserDTO actualBody = actual.getBody();
        assertThat(actualBody.getEmail(), equalTo(userDTO.getEmail()));
        assertThat(actualBody.getSalesRoleId(), notNullValue());

        salesRole = salesRoleService.findSalesRoleById(salesRole.getId());

        assertThat(salesRole.getUserList(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldUpdateExistingUser() {
        UserDTO userDTO = userController.create(testData.get(1));

        ResponseEntity<UserDTO> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/users/create", userDTO, UserDTO.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), notNullValue());

        userDTO = response.getBody();

        String currentPhoneNumber = userDTO.getPhoneNumber();
        userDTO.setPhoneNumber("91823764");

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", String.valueOf(userDTO.getId()));
        restTemplate.put("http://localhost:" + serverPort + "/api/v1/users/save/{id}", userDTO, urlVariables);

        ResponseEntity<UserDTO> actualResponse = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/users/id/" + userDTO.getId(), UserDTO.class);

        assertThat(actualResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(actualResponse.getBody(), notNullValue());

        assertThat(actualResponse.getBody().getPhoneNumber(), not(equalTo(currentPhoneNumber)));
    }

    @Test
    public void shouldAddCorrectSalesRoleFromDtoSalesRoleId() {
        UserDTO userDTO = UserDTO.builder()
                .name("Test2")
                .sureName("Testesen2")
                .email("test2.testesen2@testingco.com")
                .build();

        List<SalesRole> salesRoles = salesRoleRepository.findAll();

        assertThat(salesRoles, hasSize(greaterThan(1)));

        userDTO.setSalesRoleId(salesRoles.get(0).getId());

        userDTO = userController.create(userDTO);

        assertThat(userDTO.getId(), notNullValue());

        Optional<User> optUser = userRepository.findById(userDTO.getId());
        assertThat(optUser.isPresent(), is(true));

        User user = optUser.get();

        assertThat("User SalesRole is not set during creation.", user.getSalesRole(), notNullValue());
    }

    @Test
    public void shouldAddSalesRoleWhenUpdatingUser() throws JsonProcessingException {
        UserDTO userDTO = UserDTO.builder()
                .name("Test")
                .sureName("Testesen")
                .email("test.testesen@testingco.com")
                .build();

        List<SalesRole> salesRoles = salesRoleRepository.findAll();

        assertThat(salesRoles, hasSize(greaterThan(1)));

        userDTO.setSalesRoleId(salesRoles.get(0).getId());

        userDTO = userController.create(userDTO);

        assertThat(userDTO.getId(), notNullValue());

        Long previousSalesRoleId = userDTO.getSalesRoleId();
        userDTO.setSalesRoleId(salesRoles.get(1).getId());

        userDTO = userController.save(userDTO.getId(), userDTO);

        assertThat(userDTO.getSalesRoleId(), not(equalTo(previousSalesRoleId)));
    }

    @Test
    public void shouldFindUserByEmail() {
        String email = "test.testesen@ngn.no";

        User expected = User.builder()
                .name("Test Testesen")
                .email(email)
                .build();

        userRepository.save(expected);

        Map<String, String> params = new TreeMap<>();
        params.put("email", email);

        ResponseEntity<UserDTO> actual = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/users/email/{email}",  UserDTO.class, params);

        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        assertThat(actual.getBody().getEmail(), is(email));
    }
}
