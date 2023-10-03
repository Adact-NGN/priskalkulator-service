package no.ding.pk.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.ding.pk.config.ObjectMapperConfig;
import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.web.dto.v2.web.client.UserDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Disabled("ObjectMapper is null")
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityTestConfig.class, ModelMapperV2Config.class, ObjectMapperConfig.class})
@WebMvcTest(UserController.class)
public class UserControllerTest {

//    @Autowired
//    @Qualifier("modelMapperV2")
//    private ModelMapper modelMapper;

    @Autowired
    private SalesRoleService salesRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesRoleRepository salesRoleRepository;

//    @Autowired
//    private ObjectMapper objectMapper;

    private List<UserDTO> testData;
    private String requestPayload;

//    @BeforeEach
//    public void setup() throws IOException {
////        initializeDiscounts(discountService, objectMapper);
//        ClassLoader classLoader = getClass().getClassLoader();
//        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
//        File file = new File(Objects.requireNonNull(classLoader.getResource("user.json")).getFile());
//
//        assertThat(file.exists(), is(true));
//
//        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
//        requestPayload = json;
//
//        assertThat("JSON is empty", json, not(emptyOrNullString()));
//
//        testData = new ArrayList<>();
//
//        JSONArray results = new JSONArray(json);
//
//        for(int i = 0; i < results.length(); i++) {
//            JSONObject jsonObject = results.getJSONObject(i);
//
//            try {
//                UserDTO discount = objectMapper
//                        .readValue(jsonObject.toString(), UserDTO.class);
//                testData.add(discount);
//            } catch (JsonProcessingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//        assertThat("Test data is empty, reading in JSON file failed.", testData, not(empty()));
//
//        SalesRole kv = SalesRole.builder()
//                .roleName("KV")
//                .description("Kundeveileder")
//                .defaultPowerOfAttorneyOa(1)
//                .defaultPowerOfAttorneyFa(1)
//                .build();
//
//        SalesRole kv2 = SalesRole.builder()
//                .defaultPowerOfAttorneyFa(2)
//                .defaultPowerOfAttorneyOa(2)
//                .description("Salgskonsulent (rolle a)")
//                .roleName("SA")
//                .build();
//
//    }

    @Disabled("Move to service of repo test")
    @Test
    public void shouldPersistUserWithSalesRole() throws JsonProcessingException {
        SalesRole salesRole = salesRoleService.getAllSalesRoles().get(0);

        JSONArray jsonArray = new JSONArray(requestPayload);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        jsonObject.put("salesRoleId", salesRole.getId());
//        UserDTO userDTO = objectMapper.readValue(jsonObject.toString(), UserDTO.class);

//        assertThat(userDTO, notNullValue());
//        assertThat(userDTO.getSalesRoleId(), notNullValue());

//        ResponseEntity<UserDTO> actual = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/users/create", userDTO, UserDTO.class); // userController.create(userDTO);
//
//        assertThat(actual.getStatusCode(), equalTo(HttpStatus.OK));
//        assertThat(actual.getBody(), notNullValue());
//
//        UserDTO actualBody = actual.getBody();
//        assertThat(actualBody.getEmail(), equalTo(userDTO.getEmail()));
//        assertThat(actualBody.getSalesRoleId(), notNullValue());
//
//        salesRole = salesRoleService.findSalesRoleById(salesRole.getId());
//
//        assertThat(salesRole.getUserList(), hasSize(greaterThan(0)));
    }

    @Disabled("Move to service of repo test")
    @Test
    public void shouldUpdateExistingUser() {
        UserDTO userDTO = testData.get(1);

//        ResponseEntity<UserDTO> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/users/create", userDTO, UserDTO.class);
//
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//        assertThat(response.getBody(), notNullValue());
//
//        userDTO = response.getBody();
//
//        String currentPhoneNumber = userDTO.getPhoneNumber();
//        userDTO.setPhoneNumber("91823764");
//
//        Map<String, String> urlVariables = new HashMap<>();
//        urlVariables.put("id", String.valueOf(userDTO.getId()));
//        restTemplate.put("http://localhost:" + serverPort + "/api/v1/users/save/{id}", userDTO, urlVariables);
//
//        ResponseEntity<UserDTO> actualResponse = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/users/id/" + userDTO.getId(), UserDTO.class);
//
//        assertThat(actualResponse.getStatusCode(), equalTo(HttpStatus.OK));
//        assertThat(actualResponse.getBody(), notNullValue());
//
//        assertThat(actualResponse.getBody().getPhoneNumber(), not(equalTo(currentPhoneNumber)));
    }

    @Disabled("Move to service of repo test")
    @Test
    public void shouldAddCorrectSalesRoleFromDtoSalesRoleId() {
        UserDTO userDTO = UserDTO.builder()
                .name("Test2")
                .sureName("Testesen2")
                .email("test2.testesen2@testingco.com")
                .build();

//        List<SalesRole> salesRoles = salesRoleRepository.findAll();
//
//        assertThat(salesRoles, hasSize(greaterThan(1)));
//
//        userDTO.setSalesRoleId(salesRoles.get(0).getId());

//        userDTO = userController.create(userDTO);
//
//        assertThat(userDTO.getId(), notNullValue());
//
//        Optional<User> optUser = userRepository.findById(userDTO.getId());
//        assertThat(optUser.isPresent(), is(true));
//
//        User user = optUser.get();
//
//        assertThat("User SalesRole is not set during creation.", user.getSalesRole(), notNullValue());
    }

    @Disabled("Move to service of repo test")
    @Test
    public void shouldAddSalesRoleWhenUpdatingUser() throws JsonProcessingException {
        UserDTO userDTO = UserDTO.builder()
                .name("Test")
                .sureName("Testesen")
                .email("test.testesen@testingco.com")
                .build();

//        List<SalesRole> salesRoles = salesRoleRepository.findAll();

//        assertThat(salesRoles, hasSize(greaterThan(1)));
//
//        userDTO.setSalesRoleId(salesRoles.get(0).getId());
//
//        userDTO = userController.create(userDTO);
//
//        assertThat(userDTO.getId(), notNullValue());
//
//        Long previousSalesRoleId = userDTO.getSalesRoleId();
//        userDTO.setSalesRoleId(salesRoles.get(1).getId());
//
//        userDTO = userController.save(userDTO.getId(), userDTO);
//
//        assertThat(userDTO.getSalesRoleId(), not(equalTo(previousSalesRoleId)));
    }

    @Disabled("Move to service of repo test")
    @Test
    public void shouldFindUserByEmail() {
        String email = "test.testesen@ngn.no";

        User expected = User.builder()
                .name("Test Testesen")
                .email(email)
                .build();

//        userRepository.save(expected);
//
//        Map<String, String> params = new TreeMap<>();
//        params.put("email", email);
//
//        ResponseEntity<UserDTO> actual = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/users/email/{email}",  UserDTO.class, params);
//
//        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
//        assertThat(actual.getBody().getEmail(), is(email));
    }
}
