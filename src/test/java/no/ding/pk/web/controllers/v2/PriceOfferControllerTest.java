package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.web.client.UserDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferListDTO;
import no.ding.pk.web.dto.web.client.offer.PriceRowDTO;
import no.ding.pk.web.dto.web.client.requests.ApprovalRequest;
import no.ding.pk.web.enums.PriceOfferStatus;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
class PriceOfferControllerTest {

    @Autowired
    private UserService userService;

    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private final ObjectReader objectReader = new ObjectMapper().reader();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;

    private MockMvc mockMvc;
    private String approverEmail;
    private String salesEmployeeEmail;
    private User salesEmployee;
    private User approver;

    @BeforeEach
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        salesEmployeeEmail = "Wolfgang@farris-bad.no";
        salesEmployee = userService.findByEmail(salesEmployeeEmail);

        if(salesEmployee == null) {
            salesEmployee = User.builder()
                    .adId("ad-id-wegarijo-arha-rh-arha")
                    .jobTitle("Salgskonsulent")
                    .fullName("Wolfgang Amadeus Mozart")
                    .email(salesEmployeeEmail)
                    .associatedPlace("Larvik")
                    .department("Hvitsnippene")
                    .build();

            userService.save(salesEmployee, null);
        }

        approverEmail = "alexander.brox@ngn.no";
        approver = userService.findByEmail(approverEmail);

        if(approver == null) {
            approver = User.builder()
                    .adId("ad-ww-wegarijo-arha-rh-arha")
                    .associatedPlace("Oslo")
                    .email(approverEmail)
                    .department("Salg")
                    .fullName("Alexander Brox")
                    .name("Alexander")
                    .sureName("Brox")
                    .jobTitle("Markedskonsulent")
                    .build();

            userService.save(approver, null);
        }
    }

    @Test
    public void shouldPersistPriceOffer() throws Exception {
        setup();
        PriceOfferDTO priceOffer = createCompleteOfferDto();

        MvcResult result = mockMvc.perform(
                post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(priceOffer))
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldFailOnMissingSalesEmployeeIfGivenEmptyObject() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content("{}".getBytes())
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn();

        assertThat(result, notNullValue());
    }

    @Test
    public void shouldListAllPriceOfferForApprover() throws Exception {

        PriceOfferDTO priceOffer = createCompleteOfferDto();
        UserDTO approverDto = modelMapper.map(approver, UserDTO.class);
        priceOffer.setApprover(approverDto);

        MvcResult result = mockMvc.perform(
                        post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                                .content(objectWriter.writeValueAsString(priceOffer))
                                .with(jwt()
                                        .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                        .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));

        result = mockMvc.perform(get("/api/v2/price-offer/list/approver/" + approver.getId()).param("status", PriceOfferStatus.PENDING.getStatus()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        assertThat(contentAsString, notNullValue());
        PriceOfferDTO[] priceOffers = objectReader.readValue(contentAsString, PriceOfferDTO[].class);


        assertThat(priceOffers, arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldSetPriceOfferToNotApprovedWhenNewMaterialsIsAdded() throws Exception {
        User salesEmployee = userService.findByEmail(salesEmployeeEmail);
        User approver = userService.findByEmail(approverEmail);

        List<PriceRow> materials = createPriceRows();
        SalesOffice salesOfficeDTO = createSalesOffice(materials);
        List<SalesOffice> salesOfficeDTOs = List.of(salesOfficeDTO);
        PriceOffer priceOffer = createPriceOffer(salesEmployee, approver, salesOfficeDTOs);

        PriceOfferDTO priceOfferDTO = modelMapper.map(priceOffer, PriceOfferDTO.class);

        // Create
        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(priceOfferDTO))
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))
                        ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);

        assertThat(approver.getId(), notNullValue());
        assertThat(priceOfferDTO.getId(), notNullValue());

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .status(PriceOfferStatus.APPROVED.getStatus())
                .build();
        result = mockMvc.perform(put("/api/v2/price-offer/approval/" + approver.getId() + "/" + priceOfferDTO.getId())
                        .content(objectWriter.writeValueAsString(approvalRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Boolean approvalResult = objectReader.readValue(result.getResponse().getContentAsString(), Boolean.class);

        assertThat(approvalResult, is(true));

        result = mockMvc.perform(get("/api/v2/price-offer/id/" + priceOfferDTO.getId()).contentType(MediaType.APPLICATION_JSON)).andReturn();

        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);

        // Update
        PriceRowDTO newPriceRow = PriceRowDTO.builder()
                .material("111107")
                .manualPrice(1199.0)
                .approved(false)
                .needsApproval(true)
                .build();

        priceOfferDTO.getSalesOfficeList().get(0).getMaterialList().add(newPriceRow);

        result = mockMvc.perform(put("/api/v2/price-offer/save/" + priceOfferDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(priceOfferDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);

        assertThat(priceOfferDTO.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));
        assertThat(priceOfferDTO.getNeedsApproval(), is(true));
    }

    @Test
    public void shouldSetPriceOfferToApproved() throws Exception {

        User salesEmployee = userService.findByEmail(salesEmployeeEmail);
        User approver = userService.findByEmail(approverEmail);

        List<PriceRow> materials = createPriceRows();
        SalesOffice salesOfficeDTO = createSalesOffice(materials);
        List<SalesOffice> salesOfficeDTOs = List.of(salesOfficeDTO);
        PriceOffer priceOffer = createPriceOffer(salesEmployee, approver, salesOfficeDTOs);

        PriceOfferDTO priceOfferDTO = modelMapper.map(priceOffer, PriceOfferDTO.class);

        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(priceOfferDTO))
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))
                        ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);

        assertThat(approver.getId(), notNullValue());
        assertThat(priceOfferDTO.getId(), notNullValue());

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .status(PriceOfferStatus.APPROVED.getStatus())
                .build();
        result = mockMvc.perform(put("/api/v2/price-offer/approval/" + approver.getId() + "/" + priceOfferDTO.getId())
                        .content(objectWriter.writeValueAsString(approvalRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Boolean approvalResult = objectReader.readValue(result.getResponse().getContentAsString(), Boolean.class);

        assertThat(approvalResult, is(true));
    }

    @Test
    public void shouldListAllPriceOffersWithListDTO() throws Exception {
        User salesEmployee = userService.findByEmail(salesEmployeeEmail);
        User approver = userService.findByEmail(approverEmail);

        List<PriceRow> materials = createPriceRows();
        SalesOffice salesOfficeDTO = createSalesOffice(materials);
        List<SalesOffice> salesOfficeDTOs = List.of(salesOfficeDTO);
        PriceOffer priceOffer = createPriceOffer(salesEmployee, approver, salesOfficeDTOs);

        PriceOfferDTO priceOfferDTO = modelMapper.map(priceOffer, PriceOfferDTO.class);

        // Create
        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(priceOfferDTO))
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))
                        ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        priceOfferDTO = objectReader.readValue(result.getResponse().getContentAsString(), PriceOfferDTO.class);

        MvcResult resultList = mockMvc.perform(get("/api/v2/price-offer/list").contentType(MediaType.APPLICATION_JSON)).andReturn();

        PriceOfferListDTO[] priceOfferListDTOS = objectReader.readValue(resultList.getResponse().getContentAsString(), PriceOfferListDTO[].class);

        assertThat(priceOfferListDTOS, arrayWithSize(greaterThan(0)));
        assertThat(priceOfferListDTOS[0].getSalesEmployee().getFullName(), is(priceOfferDTO.getSalesEmployee().getFullName()));
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

    private PriceOfferDTO createCompleteOfferDto() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource("priceOfferWithZoneAndDiscount_V2.json")).getFile());

        assertThat(file.exists(), is(true));

        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, PriceOfferDTO.class);
    }
}