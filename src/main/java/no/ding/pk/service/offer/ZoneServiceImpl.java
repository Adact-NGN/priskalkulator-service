package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.offer.ZoneRepository;
import no.ding.pk.service.sap.StandardPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class ZoneServiceImpl implements ZoneService {

    private static final Logger log = LoggerFactory.getLogger(ZoneServiceImpl.class);

    private final ZoneRepository repository;
    private final PriceRowService priceRowService;
    private final StandardPriceService standardPriceService;

    @Autowired
    public ZoneServiceImpl(ZoneRepository repository, PriceRowService priceRowService,
                           StandardPriceService standardPriceService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
        this.standardPriceService = standardPriceService;
    }

    @Override
    public List<Zone> saveAll(List<Zone> zoneList, String salesOrg, String salesOffice) {
        List<Zone> returnZoneList = new ArrayList<>();

        for (Zone zone : zoneList) {
            log.debug("Zone {}", zone);

            Zone entity = getZone(zone.getId());

            entity.setZoneId(zone.getZoneId());
            entity.setPostalCode(zone.getPostalCode());
            entity.setPostalName(zone.getPostalName());
            entity.setIsStandardZone(zone.getIsStandardZone());

            if(zone.getPriceRows() != null && !zone.getPriceRows().isEmpty()) {
                Map<String, MaterialPrice> materialStdPrices = standardPriceService.getStandardPriceForSalesOrgAndSalesOfficeMap(salesOrg, salesOffice, zone.getZoneId());

                log.debug("Created key set for map: {}", materialStdPrices.isEmpty() ? "No map created" : materialStdPrices.keySet());


                List<PriceRow> materials = priceRowService.saveAll(zone.getPriceRows(), salesOrg, salesOffice, zone.getZoneId(), materialStdPrices);

                entity.setPriceRows(materials);
            }

            returnZoneList.add(entity);
        }

        log.debug("Persisted {} amount of Zones", returnZoneList.size());
        return returnZoneList;
    }

    private Zone getZone(Long zoneId) {
        if (zoneId != null) {
            Optional<Zone> optZone = repository.findById(zoneId);

            if (optZone.isPresent()) {
                return optZone.get();
            }
        }
        return new Zone();
    }
}
