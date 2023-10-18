package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.offer.SalesOfficeRepository;
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
public class SalesOfficeServiceImpl implements SalesOfficeService {

    private static final Logger log = LoggerFactory.getLogger(SalesOfficeServiceImpl.class);
    
    private final SalesOfficeRepository repository;
    
    private final PriceRowService priceRowService;
    
    private final ZoneService zoneService;

    private final StandardPriceService standardPriceService;

    @Autowired
    public SalesOfficeServiceImpl(SalesOfficeRepository repository, PriceRowService priceRowService,
                                  ZoneService zoneService,
                                  StandardPriceService standardPriceService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
        this.zoneService = zoneService;
        this.standardPriceService = standardPriceService;
    }
    
    @Override
    public List<SalesOffice> saveAll(List<SalesOffice> salesOfficeList, String customerNumber) {
        List<SalesOffice> returnList = new ArrayList<>();
        if(salesOfficeList != null && !salesOfficeList.isEmpty()) {
            for (SalesOffice salesOffice : salesOfficeList) {
                if (salesOffice == null) {
                    continue;
                }

                SalesOffice entity = getSalesOffice(salesOffice);

                entity.setCustomerNumber(salesOffice.getCustomerNumber());
                entity.setSalesOrg(salesOffice.getSalesOrg());
                entity.setSalesOffice(salesOffice.getSalesOffice());
                entity.setSalesOfficeName(salesOffice.getSalesOfficeName());
                entity.setPostalNumber(salesOffice.getPostalNumber());
                entity.setCity(salesOffice.getCity());

                Map<String, MaterialPrice> materialStdPrices = standardPriceService.getStandardPriceForSalesOrgAndSalesOfficeMap(salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), null);

                log.debug("Material prices fetched: {}", materialStdPrices.size());

                if (salesOffice.getMaterialList() != null && !salesOffice.getMaterialList().isEmpty()) {
                    log.debug("Adding Sales office material list");
                    List<PriceRow> materialList = priceRowService.saveAll(salesOffice.getMaterialList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), materialStdPrices);
                    
                    entity.setMaterialList(materialList);

                    log.debug("Finished adding Sales office material list");
                }

                if (salesOffice.getTransportServiceList() != null && !salesOffice.getTransportServiceList().isEmpty()) {
                    log.debug("Adding transport service material list");
                    List<PriceRow> transportServiceMaterialList = priceRowService.saveAll(salesOffice.getTransportServiceList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), materialStdPrices);
                    
                    entity.setTransportServiceList(transportServiceMaterialList);
                    log.debug("Finished adding transport materials");
                }

                if (salesOffice.getRentalList() != null && !salesOffice.getRentalList().isEmpty()) {
                    log.debug("Adding rental service material list");
                    List<PriceRow> rentalMaterialList = priceRowService.saveAll(salesOffice.getRentalList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), materialStdPrices);
                    
                    entity.setRentalList(rentalMaterialList);
                    log.debug("Finished adding rental materials");
                }

                entity = repository.save(entity);

                if (salesOffice.getZoneList() != null && !salesOffice.getZoneList().isEmpty()) {
                    log.debug("Adding zones service material list");
                    List<Zone> zones = zoneService.saveAll(salesOffice.getZoneList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice());

                    entity.setZoneList(zones);
                    log.debug("Finished adding zones");
                }

                returnList.add(entity);
            }
        }
        return returnList;
    }

    private SalesOffice getSalesOffice(SalesOffice salesOffice) {
        if (salesOffice.getId() != null) {
            Optional<SalesOffice> optSalesOffice = repository.findById(salesOffice.getId());

            if (optSalesOffice.isPresent()) {
                return optSalesOffice.get();
            }
        }
        return new SalesOffice();
    }
}
