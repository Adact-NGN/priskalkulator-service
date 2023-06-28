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
import java.util.Optional;

@Transactional
@Service
public class SalesOfficeServiceImpl implements SalesOfficeService {

    private static final Logger log = LoggerFactory.getLogger(SalesOfficeServiceImpl.class);
    
    private final SalesOfficeRepository repository;
    
    private final PriceRowService priceRowService;
    
    private final ZoneService zoneService;

    private final CustomerTermsService customerTermsService;

    private final PriceOfferTermsService priceOfferTermsService;

    private final StandardPriceService standardPriceService;
    
    @Autowired
    public SalesOfficeServiceImpl(SalesOfficeRepository repository, PriceRowService priceRowService, ZoneService zoneService, CustomerTermsService customerTermsService, PriceOfferTermsService priceOfferTermsService, StandardPriceService standardPriceService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
        this.zoneService = zoneService;
        this.customerTermsService = customerTermsService;
        this.priceOfferTermsService = priceOfferTermsService;
        this.standardPriceService = standardPriceService;
    }
    
    @Override
    public List<SalesOffice> saveAll(List<SalesOffice> salesOfficeList, String customerNumber) {
        List<SalesOffice> returnList = new ArrayList<>();
        if(salesOfficeList != null && salesOfficeList.size() > 0) {
            for (SalesOffice salesOffice : salesOfficeList) {
                if (salesOffice == null) {
                    continue;
                }

                SalesOffice entity = new SalesOffice();

                if (salesOffice.getId() != null) {
                    Optional<SalesOffice> optSalesOffice = repository.findById(salesOffice.getId());

                    if (optSalesOffice.isPresent()) {
                        entity = optSalesOffice.get();
                    }
                }

                entity.setCustomerNumber(salesOffice.getCustomerNumber());
                entity.setSalesOrg(salesOffice.getSalesOrg());
                entity.setSalesOffice(salesOffice.getSalesOffice());
                entity.setSalesOfficeName(salesOffice.getSalesOfficeName());
                entity.setPostalNumber(salesOffice.getPostalNumber());
                entity.setCity(salesOffice.getCity());

                List<MaterialPrice> materialStdPrices = standardPriceService.getStandardPriceForSalesOrgAndSalesOffice(salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), null);

                if (salesOffice.getMaterialList() != null && salesOffice.getMaterialList().size() > 0) {
                    log.debug("Adding Sales office material list");
                    List<PriceRow> materialList = priceRowService.saveAll(salesOffice.getMaterialList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), materialStdPrices);

                    entity.setMaterialList(materialList);

                    log.debug("Finished adding Sales office material list");
                }

                if (salesOffice.getTransportServiceList() != null && salesOffice.getTransportServiceList().size() > 0) {
                    log.debug("Adding transport service material list");
                    List<PriceRow> transportServiceMaterialList = priceRowService.saveAll(salesOffice.getTransportServiceList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), materialStdPrices);

                    entity.setTransportServiceList(transportServiceMaterialList);
                    log.debug("Finished adding transport materials");
                }

                if (salesOffice.getRentalList() != null && salesOffice.getRentalList().size() > 0) {
                    log.debug("Adding rental service material list");
                    List<PriceRow> rentalMaterialList = priceRowService.saveAll(salesOffice.getRentalList(), salesOffice.getSalesOrg(), salesOffice.getSalesOffice(), materialStdPrices);

                    entity.setRentalList(rentalMaterialList);
                    log.debug("Finished adding rental materials");
                }

                if (salesOffice.getZoneList() != null && salesOffice.getZoneList().size() > 0) {
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
}
