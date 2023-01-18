package no.ding.pk.service.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Terms;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.enums.TermsTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
class PriceOfferServiceImplTest {
    @Autowired
    private PriceOfferService service;

    @Autowired
    private UserService userService;

    @Autowired
    private SalesRoleService salesRoleService;

    void persistSalesRoles() {
        SalesRole knSalesRole = salesRoleService.findSalesRoleByRoleName("KN");

        if(knSalesRole == null) {
            knSalesRole = SalesRole.builder()
                    .roleName("KN")
                    .description("KAM nasjonalt")
                    .defaultPowerOfAttorneyOa(5)
                    .defaultPowerOfAttorneyFa(5)
                    .build();

            knSalesRole = salesRoleService.save(knSalesRole);
            System.out.println(knSalesRole);
        }

        SalesRole saSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        if(saSalesRole == null) {
            saSalesRole = SalesRole.builder()
                    .roleName("SA")
                    .description("Salgskonsulent (rolle a)")
                    .defaultPowerOfAttorneyOa(2)
                    .defaultPowerOfAttorneyFa(2)
                    .build();

            saSalesRole = salesRoleService.save(saSalesRole);

            System.out.println(saSalesRole);
        }
    }

    @Test
    public void shouldPersistPriceOffer() throws JsonProcessingException {

        persistSalesRoles();

        User alex = userService.findByEmail("alexander.brox@ngn.no");

        if(alex == null) {
            alex = userService.save(User.builder()
                            .adId("e2f1963a-072a-4414-8a0b-6a3aa6988e0c")
                            .name("Alexander")
                            .sureName("Brox")
                            .fullName("Alexander Brox")
                            .orgNr("100")
                            .resourceNr("63874")
                            .associatedPlace("Oslo")
                            .phoneNumber("95838638")
                            .email("alexander.brox@ngn.no")
                            .jobTitle("Markedskonsulent")
                            .powerOfAttorneyOA(5)
                            .powerOfAttorneyFA(3)
                            .build()
                    , null);
        }

        SalesRole knSalesRole = salesRoleService.findSalesRoleByRoleName("KN");
        knSalesRole.addUser(alex);
        knSalesRole = salesRoleService.save(knSalesRole);

        User salesEmployee = userService.findByEmail("Wolfgang@farris-bad.no");

        if(salesEmployee == null) {
            salesEmployee = User.builder()
                    .adId("ad-id-wegarijo-arha-rh-arha")
                    .jobTitle("Komponist")
                    .fullName("Wolfgang Amadeus Mozart")
                    .email("Wolfgang@farris-bad.no")
                    .associatedPlace("Larvik")
                    .department("Hvitsnippene")
                    .build();

            salesEmployee = userService.save(salesEmployee, null);
        }

        SalesRole saSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        saSalesRole.addUser(salesEmployee);
        saSalesRole = salesRoleService.save(saSalesRole);

        Terms customerTerms = Terms.builder()
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(new Date())
                .build();


        MaterialPrice residualWasteMaterialStdPrice = MaterialPrice.builder()
                .materialNumber("119901")
                .standardPrice(2456.0)
                .build();
        Material residualWasteMaterial = Material.builder()
                .materialNumber("119901")
                .designation("Restavfall")
                .materialGroupDesignation("Bl. næringsavfall")
                .priceUnit(1000)
                .quantumUnit("KG")
                .materialStandardPrice(residualWasteMaterialStdPrice)
                .build();
        PriceRow priceRow = PriceRow.builder()
                .customerPrice(2456.0)
                .discountPct(0.02)
                .showPriceInOffer(true)
                .manualPrice(2400.0)
                .discountLevel(1)
                .discountLevelPrice(56.0)
                .amount(1)
                .priceIncMva(2448.0)
                .material(residualWasteMaterial)
                .build();
        List<PriceRow> materialList = List.of(priceRow);

        MaterialPrice zoneMaterialStandardPrice = MaterialPrice.builder()
                .materialNumber("50101")
                .standardPrice(1131.0)
                .build();
        Material zoneMaterial = Material.builder()
                .materialNumber("50101")
                .designation("Lift - Utsett")
                .materialGroupDesignation("Tjeneste")
                .priceUnit(1)
                .quantumUnit("ST")
                .materialStandardPrice(zoneMaterialStandardPrice)
                .build();
        PriceRow zonePriceRow = PriceRow.builder()
                .customerPrice(1000.0)
                .discountPct(0.02)
                .showPriceInOffer(true)
                .manualPrice(900.0)
                .discountLevel(1)
                .discountLevelPrice(100.0)
                .amount(1)
                .priceIncMva(1125.0)
                .material(zoneMaterial)
                .build();
        List<PriceRow> zoneMaterialList = List.of(zonePriceRow);
        Zone zone = Zone.builder()
                .zoneId("0000000001")
                .postalCode("1601")
                .postalName("FREDRIKSTAD")
                .isStandardZone(true)
                .priceRows(zoneMaterialList)
                .build();
        List<Zone> zoneList = List.of(zone);
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("127")
                .salesOfficeName("Sarpsborg/Fredrikstad")
                .postalNumber("1601")
                .city("FREDRIKSTAD")
                .materialList(materialList)
                .zones(zoneList)
                .build();

        List<SalesOffice> salesOfficeList = List.of(salesOffice);
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("5162")
                .customerName("Europris Telem Notodden")
                .needsApproval(true)
                .approved(false)
                .customerTerms(customerTerms)
                .salesEmployee(salesEmployee)
                .salesOfficeList(salesOfficeList)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        Terms customerTerms2 = objectMapper.readValue(objectMapper.writeValueAsString(customerTerms), Terms.class);

        SalesOffice salesOffice2 = objectMapper.readValue(objectMapper.writeValueAsString(salesOffice), SalesOffice.class);

        PriceOffer priceOffer2 = PriceOffer.priceOfferBuilder()
                .customerNumber("327342")
                .customerName("Follo Ren IKS")
                .needsApproval(false)
                .approved(false)
                .customerTerms(customerTerms2)
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(salesOffice2))
                .build();

        assertThat(salesEmployee.getId(), notNullValue());

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer, notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));

        priceOffer2 = service.save(priceOffer2);

        assertThat(priceOffer2, notNullValue());
        assertThat(priceOffer2.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer2.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer2.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));
    }
}