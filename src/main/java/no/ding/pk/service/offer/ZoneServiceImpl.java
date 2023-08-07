package no.ding.pk.service.offer;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.offer.ZoneRepository;

@Transactional
@Service
public class ZoneServiceImpl implements ZoneService {

    private static final Logger log = LoggerFactory.getLogger(ZoneServiceImpl.class);

    private final ZoneRepository repository;
    private final PriceRowService priceRowService;

    @Autowired
    public ZoneServiceImpl(ZoneRepository repository, PriceRowService priceRowService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
    }

    @Override
    public List<Zone> saveAll(List<Zone> zoneList, String salesOrg, String salesOffice) {
        List<Zone> returnZoneList = new ArrayList<>();

        Map<String, Map<String, Set<String>>> discountMap = createDiscountMapForZones(zoneList, salesOrg, salesOffice);

        for(int i = 0; i < zoneList.size(); i++) {
            Zone zone = zoneList.get(i);
            log.debug("Zone {}", zone);

           Zone entity = new Zone();

           if(zone.getId() != null) {
               Optional<Zone> optZone = repository.findById(zone.getId());

               if(optZone.isPresent()) {
                   entity = optZone.get();
               }
           }

           entity.setZoneId(zone.getZoneId());
           entity.setPostalCode(zone.getPostalCode());
           entity.setPostalName(zone.getPostalName());
           entity.setIsStandardZone(zone.getIsStandardZone());

            if(zone.getPriceRows() != null && zone.getPriceRows().size() > 0) {

                List<PriceRow> materials = priceRowService.saveAll(zone.getPriceRows(), salesOrg, salesOffice, discountMap); // TODO: Fix this.

                zone.setPriceRows(materials);
            }

//            entity = repository.save(zone);

            returnZoneList.add(zone);
        }

        log.debug("Persisted {} amount of Zones", returnZoneList.size());
        return returnZoneList;
    }

    private Map<String, Map<String, Set<String>>> createDiscountMapForZones(List<Zone> zoneList, String salesOrg, String salesOffice) {
        Map<String, Map<String, Set<String>>> discountMap = new HashMap<>();

        // Collect all Material numbers in each price row lists.
        Set<String> materialNumberSet = getMaterialNumberSet(zoneList);

        // Create sales office to material number map.
        Map<String, Set<String>> salesOfficeMaterialMap = new HashMap<>();
        salesOfficeMaterialMap.put(salesOffice, materialNumberSet);

        // Add map to sales org map, sales office, material number map.
        discountMap.put(salesOrg, salesOfficeMaterialMap);

        return discountMap;
    }

    private Set<String> getMaterialNumberSet(List<Zone> zoneList) {
        if (zoneList == null || zoneList.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> materialNumberSet = new HashSet<>();

        for(Zone zone : zoneList) {
            zone.getPriceRows().forEach(priceRow -> materialNumberSet.add(priceRow.getMaterial().getMaterialNumber()));
        }

        return materialNumberSet;
    }

}
