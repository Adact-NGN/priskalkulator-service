package no.ding.pk.service.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.service.sap.StandardPriceService;
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
    private final StandardPriceService standardPriceService;

    @Autowired
    public ZoneServiceImpl(ZoneRepository repository, PriceRowService priceRowService, StandardPriceService standardPriceService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
        this.standardPriceService = standardPriceService;
    }

    @Override
    public List<Zone> saveAll(List<Zone> zoneList, String salesOrg, String salesOffice) {
        List<Zone> returnZoneList = new ArrayList<>();

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

            List<MaterialPrice> materialStdPrices = standardPriceService.getStandardPriceForSalesOrgAndSalesOffice(salesOrg, salesOffice, zone.getZoneId());

            if (zone.getPriceRows() != null && zone.getPriceRows().size() > 0) {
                List<PriceRow> materials = priceRowService.saveAll(zone.getPriceRows(), salesOrg, salesOffice, materialStdPrices);

                zone.setPriceRows(materials);
            }

            returnZoneList.add(zone);
        }

        log.debug("Persisted {} amount of Zones", returnZoneList.size());
        return returnZoneList;
    }

}
