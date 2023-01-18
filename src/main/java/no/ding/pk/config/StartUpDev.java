package no.ding.pk.config;

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
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.enums.TermsTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Profile("dev")
@Component
public class StartUpDev {

        private static final Logger log = LoggerFactory.getLogger(StartUpDev.class);
        
        private UserService userService;
        private PriceOfferService priceOfferService;
        private SalesRoleService salesRoleService;
        
        @Autowired
        public StartUpDev(UserService userService, PriceOfferService priceOfferService, SalesRoleService salesRoleService) {
                this.userService = userService;
                this.priceOfferService = priceOfferService;
                this.salesRoleService = salesRoleService;
        }
        
        @Transactional
        @PostConstruct
        public void postConstruct() {

                User alex = userService.save(User.builder()
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

                SalesRole marketConsultant = salesRoleService.findSalesRoleByRoleName("KN");
                marketConsultant.addUser(alex);
                marketConsultant = salesRoleService.save(marketConsultant);

                log.debug("Market consultant Sales Role: {}", marketConsultant);

                User salesEmployee = User.builder()
                .adId("ad-id-wegarijo-arha-rh-arha")
                .jobTitle("Komponist")
                .fullName("Wolfgang Amadeus Mozart")
                .email("Wolfgang@farris-bad.no")
                .associatedPlace("Larvik")
                .department("Hvitsnippene")
                .build();

                salesEmployee = userService.save(salesEmployee, null);

                SalesRole salesConsultant = salesRoleService.findSalesRoleByRoleName("SA");
                salesConsultant.addUser(salesEmployee);
                salesConsultant = salesRoleService.save(salesConsultant);

                log.debug("Sales consultant Sales Role: {}", salesConsultant);


                
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
                
                priceOfferService.save(priceOffer);
        }
}
