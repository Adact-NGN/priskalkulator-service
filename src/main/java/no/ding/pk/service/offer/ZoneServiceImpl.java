package no.ding.pk.service.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static Logger log = LoggerFactory.getLogger(ZoneServiceImpl.class);

    private final ZoneRepository repository;
    private final PriceRowService priceRowService;

    @Autowired
    public ZoneServiceImpl(ZoneRepository repository, PriceRowService priceRowService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
    }

    @Override
    public List<Zone> saveAll(List<Zone> zoneList) {
        List<Zone> returnZoneList = new ArrayList<>();

        for(int i = 0; i < zoneList.size(); i++) {
            Zone zone = zoneList.get(i);

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
                List<PriceRow> materials = priceRowService.saveAll(zone.getPriceRows());

                entity.setPriceRows(materials);
            }

//            entity = repository.save(zone);

            returnZoneList.add(entity);
        }

        log.debug("Persisted {} amount of Zones", returnZoneList.size());
        return returnZoneList;
    }
    
}
