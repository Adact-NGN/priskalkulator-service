package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.web.dto.ContactPersonDTO;

@Tag("integrationtest")
@Profile("itest")
@ActiveProfiles("itest")
@SpringBootTest
public class ContactPersonServiceTest {
    
    private String url = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_hs_contactperson/srvd/sap/zsd_hs_contactperson_master/0001/Contacts";
    
    private ContactPersonService service = new ContactPersonServiceImpl(url, new ObjectMapper());
    
    @BeforeEach
    public void setup() {
    }
    
    @Test
    void shouldFetchCustomersJSON() {
        List<ContactPersonDTO> contactPersonList = service.fetchContactPersons(new ArrayList<>(), null);
        
        assertThat(contactPersonList, not(empty()));
    }
    
    @Test
    void shouldFindContactPersonByNumber() {
        String contactPersonNumber = "200039946";
        List<ContactPersonDTO> actual = service.findContactPersonByNumber(contactPersonNumber);
        
        assertThat(actual, not(empty()));
        assertThat(actual.get(0).getContactPerson(), is(contactPersonNumber));
    }
    
    @Test
    public void shouldParseJsonOfContactPerson() throws FileNotFoundException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        
        File file = new File(classLoader.getResource("contactPerson.json").getFile());
        
        assertThat(file.exists(), is(true));
        
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        
        String json = IOUtils.toString(new FileInputStream(file), "UTF-8");
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        ContactPersonDTO contactPersonDTO = objectMapper.readValue(json, ContactPersonDTO.class);

        assertThat(contactPersonDTO.getContactPerson(), is(equalTo("200039946")));
    }
}
