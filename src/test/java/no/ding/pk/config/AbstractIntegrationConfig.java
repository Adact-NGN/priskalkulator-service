package no.ding.pk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import no.ding.pk.repository.SalesOfficePowerOfAttorneyRepository;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.repository.offer.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariable;

@Tag("integrationtest")
@ContextConfiguration
@Import(H2TestConfig.class)
@DataJpaTest
@ExtendWith(SystemStubsExtension.class)
@Getter
public abstract class AbstractIntegrationConfig {

    @Autowired
    private CustomerTermsRepository customerTermsRepository;

    @Autowired
    private PriceOfferRepository priceOfferRepository;

    @Autowired
    private SalesOfficeRepository salesOfficeRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialPriceRepository materialPriceRepository;

    @Autowired
    private PriceRowRepository priceRowRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesOfficePowerOfAttorneyRepository salesOfficePowerOfAttorneyRepository;

    @Autowired
    private EntityManagerFactory emFactory;

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();

    @Autowired
    private SalesRoleRepository salesRoleRepository;

    @BeforeAll
    public abstract void setup() throws IOException;

    private final static String PK_DATASOURCE_USERNAME = "PK_DATASOURCE_USERNAME";
    private final static String PK_DATASOURCE_PASSWORD = "PK_DATASOURCE_PASSWORD";
    private final static String PK_DATASOURCE_URL = "PK_DATASOURCE_URL";
    private final static String PK_SAP_USERNAME = "PK_SAP_USERNAME";
    private final static String PK_SAP_PASSWORD = "PK_SAP_PASSWORD";
    private final static String PK_SAP_API_CUSTOMER_URL = "PK_SAP_API_CUSTOMER_URL";
    private final static String PK_SAP_API_CONTACT_PERSON_URL = "PK_SAP_API_CONTACT_PERSON_URL";
    private final static String PK_SAP_API_STANDARD_PRICE_URL = "PK_SAP_API_STANDARD_PRICE_URL";
    private final static String PK_SAP_API_SALESORG_URL = "PK_SAP_API_SALESORG_URL";
    private final static String PK_SAP_API_MATERIAL_URL = "PK_SAP_API_MATERIAL_URL";
    private final static String PK_PDF_TEMPLATE_FILE_NAME = "PK_PDF_TEMPLATE_FILE_NAME";
    private final static String PK_AD_APP_ID_URI = "PK_AD_APP_ID_URI";
    private final static String PK_MSAL_AD_AUTHORITY = "PK_MSAL_AD_AUTHORITY";
    private final static String PK_MSAL_CLIENT_ID = "PK_MSAL_CLIENT_ID";
    private final static String PK_MSAL_SECRET = "PK_MSAL_SECRET";
    private final static String PK_MSAL_SCOPE = "PK_MSAL_SCOPE";
    private final static String PK_MSAL_AD_USER_INFO_SELECT_LIST = "PK_MSAL_AD_USER_INFO_SELECT_LIST";

    @SystemStub
    private static EnvironmentVariables environmentVariables;

    @BeforeAll
    public static void before() {

        withEnvironmentVariable(PK_DATASOURCE_USERNAME, "sa");
        environmentVariables.set(PK_DATASOURCE_PASSWORD, "");
        environmentVariables.set(PK_DATASOURCE_URL, "jdbc:h2:mem:testdb");
        environmentVariables.set(PK_SAP_USERNAME, "sapUsername");
        environmentVariables.set(PK_SAP_PASSWORD, "sapPassword");
        environmentVariables.set(PK_SAP_API_CUSTOMER_URL, "");
        environmentVariables.set(PK_SAP_API_CONTACT_PERSON_URL, "");
        environmentVariables.set(PK_SAP_API_STANDARD_PRICE_URL, "");
        environmentVariables.set(PK_SAP_API_SALESORG_URL, "");
        environmentVariables.set(PK_SAP_API_MATERIAL_URL, "");
        environmentVariables.set(PK_PDF_TEMPLATE_FILE_NAME, "");
        environmentVariables.set(PK_AD_APP_ID_URI, "");
        environmentVariables.set(PK_MSAL_AD_AUTHORITY, "");
        environmentVariables.set(PK_MSAL_CLIENT_ID, "");
        environmentVariables.set(PK_MSAL_SECRET, "");
        environmentVariables.set(PK_MSAL_SCOPE, "");
        environmentVariables.set(PK_MSAL_AD_USER_INFO_SELECT_LIST, "");
        environmentVariables.set("sap.api.standard.price.url", "http://saptest.norskgjenvinning.no");
        environmentVariables.set("sap.api.contact.person.url", "");
        environmentVariables.set("sap.api.salesorg.url", "");
        environmentVariables.set("sales.offices.requires.fa.approvment", "100");
    }
}
