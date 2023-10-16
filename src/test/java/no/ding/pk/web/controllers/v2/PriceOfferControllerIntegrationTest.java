package no.ding.pk.web.controllers.v2;

import com.azure.spring.cloud.autoconfigure.aad.properties.AadResourceServerProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import no.ding.pk.config.H2IntegrationTestConfig;
import no.ding.pk.config.SecurityConfig;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.UserService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.sap.StandardPriceService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.dto.web.client.offer.PriceRowDTO;
import no.ding.pk.web.dto.web.client.offer.SalesOfficeDTO;
import no.ding.pk.web.dto.web.client.offer.ZoneDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("403 Forbidden error")
@SqlConfig(commentPrefix = "#")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(value = {"/discount_db_scripts/discount_matrix.sql", "/discount_db_scripts/discount_levels.sql"})
@SpringJUnitWebConfig(classes = {H2IntegrationTestConfig.class, PriceOfferServiceConfig.class, SecurityConfig.class})
class PriceOfferControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @MockBean
    private SapMaterialService sapMaterialService;

    @MockBean
    private StandardPriceService standardPriceService;

    @MockBean
    private AadResourceServerProperties aadResourceServerProperties;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;

    @Autowired
    private PriceOfferService priceOfferService;

    @Autowired
    @Qualifier("sopoaTestService")
    private SalesOfficePowerOfAttorneyService sopoaService;

    @BeforeEach
    public void setup() {
        PriceOfferController priceOfferController1 = new PriceOfferController(priceOfferService, sopoaService, modelMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(priceOfferController1).build();
    }

    private static final String baseUrl = "/api/v2/price-offer";

    @Test
    public void shouldPersistPriceOffer() throws Exception {
        String priceOffer = getCompleteofferDtoString(null);

        String mail = "alexander.brox@ngn.no";

        userService.save(User.builder()
                .id(2L)
                .name("Alexander Brox")
                .phoneNumber("95838638")
                .email(mail)
                .build(), null);

        mockMvc.perform(post(baseUrl + "/create")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(priceOffer))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldPersistPriceOfferWithMultipleZonesWithSimilarMaterials() throws Exception {
        String filename = "priceOfferDtoV2_multipleZonePrices.json";

        userService.save(User.builder()
                .id(123L)
                .orgNr("100")
                .orgName("NG")
                .name("Kjetil")
                .jobTitle("Systemutvikler")
                        .email("kjetil.torvund.minde@ngn.no")
                .powerOfAttorneyOA(4)
                .powerOfAttorneyFA(2)
                .build(), null);

        String json = getCompleteofferDtoString(filename);

        String createUrl = "/api/v2/price-offer/create";

        MvcResult result = mockMvc.perform(post(createUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();

        PriceOfferDTO priceOfferDTOResult = new ObjectMapper().reader().readValue(resultAsString, PriceOfferDTO.class);

        assertThat(priceOfferDTOResult.getSalesOfficeList(), hasSize(1));
        SalesOfficeDTO salesOfficeDTO = priceOfferDTOResult.getSalesOfficeList().get(0);

        assertThat(salesOfficeDTO.getZoneList(), hasSize(3));
        List<ZoneDTO> zoneDTOList = salesOfficeDTO.getZoneList();
        assertThat(zoneDTOList.get(0).getMaterialList(), hasSize(6));
        assertThat(zoneDTOList.get(1).getMaterialList(), hasSize(6));
        assertThat(zoneDTOList.get(2).getMaterialList(), hasSize(6));

    }

    @Disabled("Move to service test")
    @Test
    public void shouldActivatePriceOffer() throws IOException {
        PriceOfferDTO priceOffer = createCompleteOfferDto(null);

        String createUrl = "/api/v2/price-offer/create";
//        ResponseEntity<PriceOfferDTO> createdPriceOffer = restTemplate.postForEntity(createUrl, priceOffer, PriceOfferDTO.class);

//        assertThat(createdPriceOffer.getStatusCode(), is(HttpStatus.OK));
//        assertThat(createdPriceOffer, notNullValue());

//        Optional<PriceOffer> byId = priceOfferRepository.findById(createdPriceOffer.getBody().getId());

//        assertThat(byId.isPresent(), is(true));

//        PriceOffer persistedPriceOffer = byId.get();
//        persistedPriceOffer.setPriceOfferStatus(PriceOfferStatus.APPROVED.getStatus());
//
//        priceOfferRepository.save(persistedPriceOffer);
//
//        TermsDTO customerTerms = createdPriceOffer.getBody().getCustomerTerms();
//        customerTerms.setMetalPricing("Fastpris (opp til kr -500,- pr. tonn)");
//        customerTerms.setMetalSetDateForOffer(new Date());
//        customerTerms.setPaymentCondition("15 dgr");
//        customerTerms.setInvoiceInterval("Hver 14.dag");
//
//        ActivatePriceOfferRequest offerRequest = new ActivatePriceOfferRequest(customerTerms, "Activate it");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<ActivatePriceOfferRequest> request = new HttpEntity<>(offerRequest, headers);
//
//        String activateOfferUrl = "/api/v2/price-offer/activate/" + createdPriceOffer.getBody().getSalesEmployee().getId() + "/" + createdPriceOffer.getBody().getId();
//        ResponseEntity<Boolean> actual = restTemplate.exchange(activateOfferUrl, HttpMethod.PUT, request, Boolean.class);
//
//        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
//        assertThat(actual.getBody(), is(true));
//
//        ResponseEntity<CustomerTermsDTO[]> activeCustomerTerms = restTemplate.getForEntity("/api/v1/terms/customer/list/active?salesOffice={salesOffice}&customerNumber={customerNumber}", CustomerTermsDTO[].class,
//                Map.of("salesOffice", createdPriceOffer.getBody().getSalesOfficeList().get(0).getSalesOffice(), "customerNumber", createdPriceOffer.getBody().getCustomerNumber()));
//
//        assertThat(activeCustomerTerms.getStatusCode(), is(HttpStatus.OK));
//        assertThat(activeCustomerTerms.getBody(), arrayWithSize(1));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldFailOnMissingSalesEmployeeIfGivenEmptyObject() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content("{}".getBytes())
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
                .andExpect(status().is5xxServerError())
                .andReturn();

        assertThat(result, notNullValue());
    }

    @Disabled("Move to service test")
    @Test
    public void shouldListAllPriceOfferForApprover() throws Exception {

//        PriceOfferDTO priceOffer = createCompleteOfferDto(null);
//        UserDTO approverDto = modelMapper.map(approver, UserDTO.class);
//        priceOffer.setApprover(approverDto);

//        MvcResult result = mockMvc.perform(
//                        post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
//                                .content(objectWriter.writeValueAsString(priceOffer))
//                                .with(jwt()
//                                        .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
//                                        .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
//                .andExpect(status().isOk())
//                .andReturn();
//        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
//
//        result = mockMvc.perform(get("/api/v2/price-offer/list/approver/" + approver.getId()).param("status", PriceOfferStatus.PENDING.getStatus()))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String contentAsString = result.getResponse().getContentAsString();
//
//        assertThat(contentAsString, notNullValue());
//        PriceOfferDTO[] priceOffers = objectReader.readValue(contentAsString, PriceOfferDTO[].class);
//
//
//        assertThat(priceOffers, arrayWithSize(greaterThan(0)));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldSetPriceOfferToNotApprovedWhenNewMaterialsIsAdded() throws Exception {
//        User salesEmployee = userService.findByEmail(salesEmployeeEmail);
//        User approver = userService.findByEmail(approverEmail);

//        List<PriceRow> materials = createPriceRows();
//        SalesOffice salesOfficeDTO = createSalesOffice(materials);
//        List<SalesOffice> salesOfficeDTOs = List.of(salesOfficeDTO);
//        PriceOffer priceOffer = createPriceOffer(salesEmployee, approver, salesOfficeDTOs);
//
//        PriceOfferDTO priceOfferDTO = modelMapper.map(priceOffer, PriceOfferDTO.class);

        // Create
//        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
//                        .content(objectWriter.writeValueAsString(priceOfferDTO))
//                        .with(jwt()
//                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
//                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))
//                        ))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);
//
//        assertThat(approver.getId(), notNullValue());
//        assertThat(priceOfferDTO.getId(), notNullValue());
//
//        ApprovalRequest approvalRequest = ApprovalRequest.builder()
//                .status(PriceOfferStatus.APPROVED.getStatus())
//                .build();
//        result = mockMvc.perform(put("/api/v2/price-offer/approval/" + approver.getId() + "/" + priceOfferDTO.getId())
//                        .content(objectWriter.writeValueAsString(approvalRequest))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        Boolean approvalResult = objectReader.readValue(result.getResponse().getContentAsString(), Boolean.class);
//
//        assertThat(approvalResult, is(true));
//
//        result = mockMvc.perform(get("/api/v2/price-offer/id/" + priceOfferDTO.getId()).contentType(MediaType.APPLICATION_JSON)).andReturn();
//
//        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);
//
//        // Update
//        PriceRowDTO newPriceRow = PriceRowDTO.builder()
//                .material("111107")
//                .manualPrice(1199.0)
//                .approved(false)
//                .needsApproval(true)
//                .build();
//
//        priceOfferDTO.getSalesOfficeList().get(0).getMaterialList().add(newPriceRow);
//
//        result = mockMvc.perform(put("/api/v2/price-offer/save/" + priceOfferDTO.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectWriter.writeValueAsString(priceOfferDTO)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);
//
//        assertThat(priceOfferDTO.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));
//        assertThat(priceOfferDTO.getNeedsApproval(), is(true));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldSetPriceOfferToApproved() throws Exception {

//        User salesEmployee = userService.findByEmail(salesEmployeeEmail);
//        User approver = userService.findByEmail(approverEmail);
//
//        List<PriceRow> materials = createPriceRows();
//        SalesOffice salesOfficeDTO = createSalesOffice(materials);
//        List<SalesOffice> salesOfficeDTOs = List.of(salesOfficeDTO);
//        PriceOffer priceOffer = createPriceOffer(salesEmployee, approver, salesOfficeDTOs);
//
//        PriceOfferDTO priceOfferDTO = modelMapper.map(priceOffer, PriceOfferDTO.class);

//        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectWriter.writeValueAsString(priceOfferDTO))
//                        .with(jwt()
//                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
//                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))
//                        ))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);
//
//        assertThat(approver.getId(), notNullValue());
//        assertThat(priceOfferDTO.getId(), notNullValue());
//
//        ApprovalRequest approvalRequest = ApprovalRequest.builder()
//                .status(PriceOfferStatus.APPROVED.getStatus())
//                .build();
//        result = mockMvc.perform(put("/api/v2/price-offer/approval/" + approver.getId() + "/" + priceOfferDTO.getId())
//                        .content(objectWriter.writeValueAsString(approvalRequest))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        Boolean approvalResult = objectReader.readValue(result.getResponse().getContentAsString(), Boolean.class);
//
//        assertThat(approvalResult, is(true));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldListAllPriceOffersWithListDTO() throws Exception {
//        User salesEmployee = userService.findByEmail(salesEmployeeEmail);
//        User approver = userService.findByEmail(approverEmail);
//
//        List<PriceRow> materials = createPriceRows();
//        SalesOffice salesOfficeDTO = createSalesOffice(materials);
//        List<SalesOffice> salesOfficeDTOs = List.of(salesOfficeDTO);
//        PriceOffer priceOffer = createPriceOffer(salesEmployee, approver, salesOfficeDTOs);
//
//        PriceOfferDTO priceOfferDTO = modelMapper.map(priceOffer, PriceOfferDTO.class);

        // Create
        String createUrl = "/api/v2/price-offer/create";
//        ResponseEntity<PriceOfferDTO> result = restTemplate.postForEntity(createUrl, priceOfferDTO, PriceOfferDTO.class);
//
//        priceOfferDTO = result.getBody();
//
//        MvcResult resultList = mockMvc.perform(get("/api/v2/price-offer/list").contentType(MediaType.APPLICATION_JSON)).andReturn();
//
//        PriceOfferListDTO[] priceOfferListDTOS = objectReader.readValue(resultList.getResponse().getContentAsString(), PriceOfferListDTO[].class);
//
//        assertThat(priceOfferListDTOS, arrayWithSize(greaterThan(0)));
//        assertThat(priceOfferListDTOS[0].getSalesEmployee().getFullName(), is(priceOfferDTO.getSalesEmployee().getFullName()));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldPersistPriceOfferWithDeviceType() throws IOException {
        PriceOfferDTO priceOfferDTO = createCompleteOfferDto("priceOfferWithDeviceType.json");

        String createUrl = "/api/v2/price-offer/create";
//        ResponseEntity<PriceOfferDTO> actual = restTemplate.postForEntity(createUrl, priceOfferDTO, PriceOfferDTO.class);
//
//        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
//
//        PriceOfferDTO actualPo = actual.getBody();
//
//        assertThat(actualPo, notNullValue());
//        Set<String> deviceTypes = new HashSet<>();
//        actualPo.getSalesOfficeList().forEach(salesOfficeDTO -> salesOfficeDTO.getMaterialList().forEach(priceRowDTO -> deviceTypes.add(priceRowDTO.getDeviceType())));
//
//        assertThat(deviceTypes.size(), greaterThan(1));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldPersistPriceOfferWithSeveralSalesOffices() throws IOException {
        PriceOfferDTO priceOfferDTO = createCompleteOfferDto("priceOfferDtoMultipleSO.json");

        String createUrl = "/api/v2/price-offer/create";
//        ResponseEntity<PriceOfferDTO> actual = restTemplate.postForEntity(createUrl, priceOfferDTO, PriceOfferDTO.class);
//
//        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
//
//        PriceOfferDTO actualPo = actual.getBody();
//
//        assertThat(actualPo, notNullValue());
//        List<SalesOfficeDTO> salesOffices = actualPo.getSalesOfficeList();
//
//        assertThat(salesOffices.size(), greaterThan(1));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldPersistPriceOfferWithSeveralSalesOfficesAndZones() throws IOException {
        PriceOfferDTO priceOfferDTO = createCompleteOfferDto("priceOfferWithMultipleSoAndZones.json");

        String createUrl = "/api/v2/price-offer/create";
//        ResponseEntity<PriceOfferDTO> actual = restTemplate.postForEntity(createUrl, priceOfferDTO, PriceOfferDTO.class);
//
//        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
//
//        PriceOfferDTO actualPo = actual.getBody();
//
//        assertThat(actualPo, notNullValue());
//        List<SalesOfficeDTO> salesOffices = actualPo.getSalesOfficeList();
//
//        assertThat(salesOffices.size(), greaterThan(1));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldUpdatePriceOfferAfterChangesFromUser() throws IOException {
        PriceOfferDTO priceOfferDTO = createCompleteOfferDto("priceOfferWithMultipleSoAndZones.json");

        String createUrl = "/api/v2/price-offer/create";
//        ResponseEntity<PriceOfferDTO> createdEntity = restTemplate.postForEntity(createUrl, priceOfferDTO, PriceOfferDTO.class);
//
//        assertThat(createdEntity.getStatusCode(), is(HttpStatus.OK));
//
//        PriceOfferDTO createdResponse = createdEntity.getBody();
//
//        PriceOfferDTO updatedPriceOfferDTO = createCompleteOfferDto("updatedPriceOfferAfterCreation.json");
//
//        copyIdsFromCreatedPriceOfferToUpdated(createdResponse, updatedPriceOfferDTO);
//
//        String updateUrl = "/api/v2/price-offer/save/" + createdResponse.getId();
//        restTemplate.put(updateUrl, updatedPriceOfferDTO);
//
//        ResponseEntity<PriceOfferDTO> actual = restTemplate.getForEntity("/api/v2/price-offer/id/" + createdResponse.getId(), PriceOfferDTO.class);
//
//        assertThat(actual.getStatusCode(), is(HttpStatus.OK));


    }

    private void copyIdsFromCreatedPriceOfferToUpdated(PriceOfferDTO from, PriceOfferDTO to) {
        to.setId(from.getId());

        for (SalesOfficeDTO officeDTO : from.getSalesOfficeList()) {
            SalesOfficeDTO toSalesOffice = to.getSalesOfficeList().stream().filter(salesOfficeDTO -> salesOfficeDTO.getSalesOffice().equals(officeDTO.getSalesOffice())).findAny().orElseGet(null);

            if(toSalesOffice == null) {
                continue;
            }

            toSalesOffice.setId(officeDTO.getId());

            updatePriceRowsId(officeDTO.getMaterialList(), toSalesOffice.getMaterialList());
            updatePriceRowsId(officeDTO.getRentalList(), toSalesOffice.getRentalList());
            updatePriceRowsId(officeDTO.getTransportServiceList(), toSalesOffice.getTransportServiceList());

            for (ZoneDTO fromZoneDTO : officeDTO.getZoneList()) {
                ZoneDTO toZoneDto = toSalesOffice.getZoneList().stream().filter(zoneDTO -> zoneDTO.getNumber().equals(fromZoneDTO.getNumber())).findAny().orElse(null);

                if(toZoneDto == null) {
                    continue;
                }

                updatePriceRowsId(fromZoneDTO.getMaterialList(), toZoneDto.getMaterialList());
            }
        }
    }

    private void updatePriceRowsId(List<PriceRowDTO> fromPriceRowList, List<PriceRowDTO> toPriceRowList) {
        if(fromPriceRowList == null) {
            return;
        }
        for (PriceRowDTO fromPriceRowDTO : fromPriceRowList) {
            PriceRowDTO toPriceRowDto = toPriceRowList.stream().filter(priceRow -> priceRow.getMaterial().equals(fromPriceRowDTO.getMaterial())).findAny().orElse(null);

            if(toPriceRowDto != null) {
                toPriceRowDto.setId(fromPriceRowDTO.getId());
            }
        }
    }

    private static PriceOffer createPriceOffer(User salesEmployee, User approver, List<SalesOffice> salesOfficeDTOs) {
        return PriceOffer.priceOfferBuilder()
                .customerNumber("102520")
                .customerName("Vest-Telemark Rørleggerforretning AS")
                .salesOfficeList(salesOfficeDTOs)
                .salesEmployee(salesEmployee)
                .approver(approver)
                .needsApproval(true)
                .build();
    }

    private static SalesOffice createSalesOffice(List<PriceRow> materials) {
        return SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("104")
                .city("Skien")
                .materialList(materials)
                .build();
    }

    private List<PriceRow> createPriceRows() {
        List<PriceRow> returnList = new ArrayList<>();

        Map<String, Double> prMatMap = Map.of("111101", 1199.0, "111102", 1199.0, "111103", 1199.0,
                "111104", 1199.0, "111105", 1199.0, "171111", 519.0,
                "PR-1111", 1684.0);

        for (String key : prMatMap.keySet()) {
            Material material = Material.builder()
                    .materialNumber(key)
                    .build();

            PriceRow pr = PriceRow.builder()
                    .material(material)
                    .manualPrice(prMatMap.get(key))
                    .needsApproval(true)
                    .approved(false)
                    .build();

            returnList.add(pr);
        }

        return returnList;
    }

    private PriceOfferDTO createCompleteOfferDto(String filename) throws IOException {
        String json = getCompleteofferDtoString(filename);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, PriceOfferDTO.class);
    }

    private String getCompleteofferDtoString(String filename) throws IOException {
        String inputFileName = filename;
        if (StringUtils.isBlank(filename)) {
            inputFileName = "priceOfferWithZoneAndDiscount_V2.json";
        }

        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource(inputFileName)).getFile());

        assertThat(file.exists(), is(true));

        return IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
    }
}