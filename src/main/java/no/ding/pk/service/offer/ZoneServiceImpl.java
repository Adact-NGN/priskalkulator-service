package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.offer.ZoneRepository;
import no.ding.pk.service.DiscountService;
import no.ding.pk.service.sap.StandardPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class ZoneServiceImpl implements ZoneService {

    private static final Logger log = LoggerFactory.getLogger(ZoneServiceImpl.class);

    private final ZoneRepository repository;
    private final PriceRowService priceRowService;
    private final DiscountService discountService;
    private final StandardPriceService standardPriceService;

    @Autowired
    public ZoneServiceImpl(ZoneRepository repository, PriceRowService priceRowService, DiscountService discountService,
                           StandardPriceService standardPriceService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
        this.discountService = discountService;
        this.standardPriceService = standardPriceService;
    }

    @Override
    public List<Zone> saveAll(List<Zone> zoneList, String salesOrg, String salesOffice) {
        List<Zone> returnZoneList = new ArrayList<>();

        Map<String, Map<String, Map<String, Discount>>> discountMap = createDiscountMapForZones(zoneList, salesOrg, salesOffice);

        for (Zone zone : zoneList) {
            log.debug("Zone {}", zone);

            Zone entity = new Zone();

            if (zone.getId() != null) {
                Optional<Zone> optZone = repository.findById(zone.getId());

                if (optZone.isPresent()) {
                    entity = optZone.get();
                }
            }

            entity.setZoneId(zone.getZoneId());
            entity.setPostalCode(zone.getPostalCode());
            entity.setPostalName(zone.getPostalName());
            entity.setIsStandardZone(zone.getIsStandardZone());

            Map<String, MaterialPrice> materialStdPrices = standardPriceService.getStandardPriceForSalesOrgAndSalesOfficeMap(salesOrg, salesOffice, zone.getZoneId());

            if(zone.getPriceRows() != null && zone.getPriceRows().size() > 0) {
                List<PriceRow> materials = priceRowService.saveAll(zone.getPriceRows(), salesOrg, salesOffice, zone.getZoneId(), materialStdPrices, discountMap);

                entity.setPriceRows(materials);
            }

            returnZoneList.add(entity);
        }

        log.debug("Persisted {} amount of Zones", returnZoneList.size());
        return returnZoneList;
    }

    private Map<String, Map<String, Map<String, Discount>>> createDiscountMapForZones(List<Zone> zoneList, String salesOrg, String salesOffice) {
        Map<String, Map<String, Set<String>>> salesOrgSalesOfficeMaterialSet = new HashMap<>();

        // Collect all Material numbers in each price row lists.
        Set<String> materialNumberSet = getMaterialNumberSet(zoneList);



        // Create sales office to material number map.
        Map<String, Set<String>> salesOfficeMaterialMap = new HashMap<>();
        salesOfficeMaterialMap.put(salesOffice, materialNumberSet);

        // Add map to sales org map, sales office, material number map.
        salesOrgSalesOfficeMaterialSet.put(salesOrg, salesOfficeMaterialMap);

        Map<String, Map<String, Map<String, Discount>>> orgOfficeMaterialDiscount = new HashMap<>();

        orgOfficeMaterialDiscount.put(salesOrg, new HashMap<>());

        Map<String, Map<String, Discount>> salesOfficeToMaterialDiscountMap = new HashMap<>();
        Map<String, Discount> materialNumberToDiscountMap = new HashMap<>();
        List<String> materialNumbers = salesOrgSalesOfficeMaterialSet.get(salesOrg).get(salesOffice).stream().toList();

        List<Discount> discounts = discountService.findAllDiscountForDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(salesOrg, salesOffice, materialNumbers);

        if(discounts != null && !discounts.isEmpty()) {
            discounts.forEach(discount -> materialNumberToDiscountMap.put(discount.getMaterialNumber(), discount));
        }

        salesOfficeToMaterialDiscountMap.put(salesOffice, materialNumberToDiscountMap);

        orgOfficeMaterialDiscount.put(salesOrg, salesOfficeToMaterialDiscountMap);

        return orgOfficeMaterialDiscount;
    }

    private Set<String> getMaterialNumberSet(List<Zone> zoneList) {
        if (zoneList == null || zoneList.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> materialNumberSet = new HashSet<>();

        for(Zone zone : zoneList) {
            if(zone.getPriceRows() == null) {
                continue;
            }
            zone.getPriceRows().forEach(priceRow -> materialNumberSet.add(priceRow.getMaterial().getMaterialNumber()));
        }

        return materialNumberSet;
    }

}
