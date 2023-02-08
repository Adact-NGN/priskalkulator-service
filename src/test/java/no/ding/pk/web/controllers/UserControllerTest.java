package no.ding.pk.web.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.ding.pk.web.dto.web.client.SalesRoleDTO;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.web.mappers.MapperService;

@SpringBootTest
@TestPropertySource("/h2-db.properties")
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private SalesRoleService salesRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MapperService mapperService;

    private List<User> testData;
    private String requestPayload;

    @BeforeEach
    public void setup() throws FileNotFoundException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(classLoader.getResource("user.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), "UTF-8");
        requestPayload = json;

        assertThat("JSON is empty", json, not(emptyOrNullString()));

        testData = new ArrayList<>();

        JSONArray results = new JSONArray(json);

        for(int i = 0; i < results.length(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);

            try {
                User discount = objectMapper.readValue(jsonObject.toString(), User.class);
                testData.add(discount);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        assertThat("Test data is empty, reading in JSON file failed.", testData, not(empty()));

    }
 
    @Test
    public void shouldPersistUserWithSalesRole() throws JsonMappingException, JsonProcessingException {
        SalesRole salesRole = testData.get(0).getSalesRole();

        salesRole = salesRoleService.save(salesRole);

        JSONArray jsonArray = new JSONArray(requestPayload);

        UserDTO userDTO = objectMapper.readValue(jsonArray.getJSONObject(0).toString(), UserDTO.class);

        assertThat(userDTO, notNullValue());
        SalesRoleDTO salesRoleDTO = mapperService.toSalesRoleDTO(salesRole);
        userDTO.setSalesRole(salesRoleDTO);

        User actual = userController.create(userDTO);

        assertThat(actual.getEmail(), equalTo(userDTO.getEmail()));
        assertThat(actual.getSalesRole(), notNullValue());

        salesRole = salesRoleService.findSalesRoleById(salesRole.getId());

        assertThat(salesRole.getUserList(), hasSize(greaterThan(0)));
    }
}
