package no.ding.pk.config;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTerms;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.service.UserService;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.service.sap.StandardPriceService;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.enums.SalesRoleName;
import no.ding.pk.web.enums.TermsTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Profile("dev")
@Component
public class StartUpDev {

        private static final Logger log = LoggerFactory.getLogger(StartUpDev.class);

        private final UserService userService;
        private final PriceOfferService priceOfferService;
        private final SalesRoleService salesRoleService;
        private final StandardPriceService priceService;
        private final MaterialService materialService;

        @Autowired
        public StartUpDev(UserService userService,
                          PriceOfferService priceOfferService,
                          SalesRoleService salesRoleService,
                          StandardPriceService priceService,
                          MaterialService materialService) {
                this.userService = userService;
                this.priceOfferService = priceOfferService;
                this.salesRoleService = salesRoleService;
                this.priceService = priceService;
                this.materialService = materialService;
        }

        @Transactional
        @PostConstruct
        public void postConstruct() {

                User kjetil = userService.findByEmail("kjetil.torvund.minde@ngn.no");

                if(kjetil == null) {
                        kjetil = createAndGetUser("dc804853-6a82-4022-8eb5-244fff724af2", "Kjetil",
                                "Torvund Minde", "Kjetil Torvund Minde", "100",
                                "Norsk Gjenvinning", "Larvik", "90135757",
                                "kjetil.torvund.minde@ngn.no",
                                "Systemutvikler", 5, 5, null);
                }

                SalesRole admin = salesRoleService.findSalesRoleByRoleName(SalesRoleName.Superadmin.name());

                if(admin != null && admin.getUserList() != null && !admin.getUserList().contains(kjetil)) {
                        admin.addUser(kjetil);
                        salesRoleService.save(admin);
                }

                User alex = userService.findByEmail("alexander.brox@ngn.no");

                if(alex == null) {

                        alex = createAndGetUser("e2f1963a-072a-4414-8a0b-6a3aa6988e0c", "Alexander",
                                        "Brox", "Alexander Brox","100","Norsk Gjenvinning",
                                "Oslo", "95838638", "alexander.brox@ngn.no",
                                "Markedskonsulent", 5, 3, "63874");
                }

                SalesRole marketConsultant = salesRoleService.findSalesRoleByRoleName(SalesRoleName.Kundeveileder.name());

                if(marketConsultant != null && marketConsultant.getUserList() != null && !marketConsultant.getUserList().contains(alex)) {
                        marketConsultant.addUser(alex);
                        marketConsultant = salesRoleService.save(marketConsultant);
                }

                log.debug("Market consultant Sales Role: {}", marketConsultant);

                User salesEmployee = userService.findByEmail("Wolfgang@farris-bad.no");

                if(salesEmployee == null) {
                        salesEmployee = User.builder()
                                .adId("ad-id-wegarijo-arha-rh-arha")
                                .orgNr("100")
                                .jobTitle("Komponist")
                                .fullName("Wolfgang Amadeus Mozart")
                                .email("Wolfgang@farris-bad.no")
                                .associatedPlace("Larvik")
                                .department("Hvitsnippene")
                                .build();

                        salesEmployee = userService.save(salesEmployee, null);
                }

                SalesRole salesConsultant = salesRoleService.findSalesRoleByRoleName(SalesRoleName.SalgskonsulentRolleA.name());

                if(salesConsultant != null && salesConsultant.getUserList() != null && !salesConsultant.getUserList().contains(salesEmployee)) {
                        salesConsultant.addUser(salesEmployee);
                        salesConsultant = salesRoleService.save(salesConsultant);
                }

                log.debug("Sales consultant Sales Role: {}", salesConsultant);

                PriceOfferTerms customerTerms = PriceOfferTerms.builder()
                        .contractTerm(TermsTypes.GeneralTerms.getValue())
                        .agreementStartDate(new Date())
                        .build();

                List<MaterialStdPriceDTO> materialDTOs = priceService.getStdPricesForSalesOfficeAndSalesOrg("100", "100", null);

                log.debug("Material DTO list size: {}", materialDTOs.size());

                if(materialDTOs.size() == 0) {
                        log.debug("No materials was received from service.");
                        return;
                }

                List<String> materialNumberList = List.of("119901", "122110", "132201");
                List<PriceRow> materialList = createPriceRowList(materialNumberList, materialDTOs); // List.of(priceRow);

                List<String> zone0MaterialNumberList = List.of("50201", "50203", "50101", "50102", "50104");
                List<PriceRow> zone0MaterialList = createPriceRowList(zone0MaterialNumberList, materialDTOs);
                Zone zone0 = createZone("0000000001", "1601", "FREDRIKSTAD", true, zone0MaterialList);

                List<String> zone1MaterialNumberList = List.of("50201", "50203", "50101", "50102", "50104");
                List<PriceRow> zone1MaterialList = createPriceRowList(zone1MaterialNumberList, materialDTOs);
                Zone zone1 = createZone("0000000002", "1601", "FREDRIKSTAD", false, zone1MaterialList);

                List<Zone> zoneList = List.of(zone0, zone1);

                String flatbedTransport = "50305";
                PriceRow flatBedMaterial = createPriceRow(materialDTOs, flatbedTransport);
                List<String> flatBedCombinedMaterialNumbers = List.of("50321", "B-0660");
                List<PriceRow> flatBedCombinedMaterialList = createPriceRowList(flatBedCombinedMaterialNumbers, materialDTOs);

                flatBedMaterial.setCombinedMaterials(flatBedCombinedMaterialList);

                String compressionTruckTransport = "50405";
                PriceRow compressionMaterial = createPriceRow(materialDTOs, compressionTruckTransport);
                List<String> compressionTruckTransportCombinedMaterialNumbers = List.of("50421", "B-0140", "C-10CL");
                List<PriceRow> compressionTruckTransportCombinedMaterialList = createPriceRowList(compressionTruckTransportCombinedMaterialNumbers, materialDTOs);

                compressionMaterial.setCombinedMaterials(compressionTruckTransportCombinedMaterialList);

                List<PriceRow> transportMaterialList = new ArrayList<>();
                transportMaterialList.add(flatBedMaterial);
                transportMaterialList.add(compressionMaterial);

                List<String> rentMaterialNumberList = List.of("B-0660", "C-10CL", "C-35K", "B-0B-E");
                List<PriceRow> rentalMaterialList = createPriceRowList(rentMaterialNumberList, materialDTOs);

                SalesOffice salesOffice = SalesOffice.builder()
                        .salesOrg("100")
                        .salesOffice("127")
                        .salesOfficeName("Sarpsborg/Fredrikstad")
                        .postalNumber("1601")
                        .city("FREDRIKSTAD")
                        .materialList(materialList)
                        .transportServiceList(transportMaterialList)
                        .rentalList(rentalMaterialList)
                        .zoneList(zoneList)
                        .build();

                List<SalesOffice> salesOfficeList = List.of(salesOffice);
                PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                        .customerNumber("5162")
                        .customerName("Europris Telem Notodden")
                        .needsApproval(true)
                        .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                        .salesEmployee(salesEmployee)
                        .salesOfficeList(salesOfficeList)
                        .approver(kjetil)
                        .build();

                priceOfferService.save(priceOffer);
        }

        private User createAndGetUser(String adId, String name, String sureName, String fullName, String orgNr,
                                      String orgName, String associatedPlace, String phoneNumber, String email,
                                      String jobTitle, int powerOfAttorneyFA, int powerOfAttorneyOA, String resourceNr) {
                return userService.save(
                        User.builder()
                                .adId(adId)
                                .name(name)
                                .sureName(sureName)
                                .fullName(fullName)
                                .orgNr(orgNr)
                                .orgName(orgName)
                                .associatedPlace(associatedPlace)
                                .phoneNumber(phoneNumber)
                                .email(email)
                                .jobTitle(jobTitle)
                                .powerOfAttorneyFA(powerOfAttorneyFA)
                                .powerOfAttorneyOA(powerOfAttorneyOA)
                                .resourceNr(resourceNr)
                                .build(),
                        null);
        }

        private List<PriceRow> createPriceRowList(List<String> materialNumberList, List<MaterialStdPriceDTO> materialDTOs) {
                List<PriceRow> returnList = new ArrayList<>();

                for (String materialNumber : materialNumberList) {
                        PriceRow priceRow = createPriceRow(materialDTOs, materialNumber);
                        returnList.add(priceRow);
                }

                return returnList;
        }

        private PriceRow createPriceRow(List<MaterialStdPriceDTO> materialDTOs, String materialNumber) {
                MaterialStdPriceDTO materialDTO = materialDTOs.stream().filter(obj -> obj.getMaterial().equalsIgnoreCase(materialNumber)).findAny().orElse(null);
                log.debug("Found materialDTO: {}", materialDTO);
                MaterialPrice residualWasteMaterialStdPrice = createMaterialStdPrice(materialNumber, materialDTO.getStandardPrice());
                Material material = createMaterial(materialNumber, materialDTO, residualWasteMaterialStdPrice);

                material = materialService.save(material);

                return createPriceRow(materialDTO.getStandardPrice(), 0.02, true, materialDTO.getStandardPrice(), 1, 56.0, 1, materialDTO.getStandardPrice(), material);
        }

        private Zone createZone(String zoneId, String postalCode, String postalName, boolean isStandardZone, List<PriceRow> zoneMaterialList) {
                return Zone.builder()
                        .zoneId(zoneId)
                        .postalCode(postalCode)
                        .postalName(postalName)
                        .isStandardZone(isStandardZone)
                        .priceRows(zoneMaterialList)
                        .build();
        }

        private PriceRow createPriceRow(Double customerPrice,
                                        Double discountPct,
                                        boolean showPriceInOffer,
                                        Double manualPrice,
                                        int discountLevel,
                                        Double discountLevelPrice,
                                        int amount,
                                        Double priceIncMva,
                                        Material residualWasteMaterial) {
                return PriceRow.builder()
                        .customerPrice(customerPrice)
                        .discountPct(discountPct)
                        .showPriceInOffer(showPriceInOffer)
                        .manualPrice(manualPrice)
                        .discountLevel(discountLevel)
                        .discountLevelPrice(discountLevelPrice)
                        .amount(amount)
                        .priceIncMva(priceIncMva)
                        .material(residualWasteMaterial)
                        .build();
        }

        private Material createMaterial(String material, MaterialStdPriceDTO materialDTO,
                                        MaterialPrice residualWasteMaterialStdPrice) {
                log.debug("MaterialDTO PricingUnit: {} -> {}", materialDTO.getPricingUnit(), Integer.parseInt(materialDTO.getPricingUnit()));
                return Material.builder()
                        .materialNumber(material)
                        .designation(materialDTO.getDesignation())
                        .materialGroupDesignation(materialDTO.getMaterialTypeDesignation())
                        .materialTypeDescription(materialDTO.getMaterialTypeDesignation())
                        .pricingUnit(Integer.parseInt(materialDTO.getPricingUnit()))
                        .scaleQuantum(materialDTO.getScaleQuantum())
                        .quantumUnit(materialDTO.getQuantumUnit())
                        .materialStandardPrice(residualWasteMaterialStdPrice)
                        .build();
        }

        private MaterialPrice createMaterialStdPrice(String materialNumber, Double standardPrice) {
                return MaterialPrice.builder()
                        .materialNumber(materialNumber)
                        .standardPrice(standardPrice)
                        .build();
        }
}
