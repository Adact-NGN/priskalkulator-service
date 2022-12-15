package no.ding.pk.service.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.offer.SalesOfficeRepository;

@Transactional
@Service
public class SalesOfficeServiceImpl implements SalesOfficeService {
    
    private SalesOfficeRepository repository;
    
    private PriceRowService priceRowService;
    
    private ZoneService zoneService;
    
    @Autowired
    public SalesOfficeServiceImpl(SalesOfficeRepository repository, PriceRowService priceRowService, ZoneService zoneService) {
        this.repository = repository;
        this.priceRowService = priceRowService;
        this.zoneService = zoneService;
    }
    
    @Override
    public List<SalesOffice> saveAll(List<SalesOffice> salesOfficeList) {
        List<SalesOffice> returnList = new ArrayList<>();
        if(salesOfficeList != null && salesOfficeList.size() > 0) {
            for(int j = 0; j < salesOfficeList.size(); j++) {
                SalesOffice salesOffice = salesOfficeList.get(j);
                
                SalesOffice entity = new SalesOffice();
                
                if(salesOffice.getId() != null) {
                    Optional<SalesOffice> optSalesOffice = repository.findById(salesOffice.getId());
                    
                    if(optSalesOffice.isPresent()) {
                        entity = optSalesOffice.get();
                    }
                }
                
                entity.setCustomerNumber(salesOffice.getCustomerNumber());
                entity.setSalesOrg(salesOffice.getSalesOrg());
                entity.setName(salesOffice.getName());
                entity.setPostalNumber(salesOffice.getPostalNumber());
                entity.setCity(salesOffice.getCity());
                
                if(salesOffice.getMaterialList() != null && salesOffice.getMaterialList().size() > 0) {
                    List<PriceRow> materialList = priceRowService.saveAll(salesOffice.getMaterialList());
                    
                    entity.setMaterialList(materialList);
                }
                
                if(salesOffice.getTransportServiceList() != null && salesOffice.getTransportServiceList().size() > 0) {
                    List<PriceRow> transportServiceMaterialList = priceRowService.saveAll(salesOffice.getTransportServiceList());
                    
                    entity.setTransportServiceList(transportServiceMaterialList);
                }
                
                if(salesOffice.getRentalList() != null && salesOffice.getRentalList().size() > 0) {
                    List<PriceRow> rentalMaterialList = priceRowService.saveAll(salesOffice.getRentalList());
                    
                    entity.setRentalList(rentalMaterialList);
                }
                
                if(salesOffice.getZoneList() != null && salesOffice.getZoneList().size() > 0) {
                    List<Zone> zones = zoneService.saveAll(salesOffice.getZoneList());
                    
                    entity.setZoneList(zones);
                }
                
                entity = repository.save(entity);
                
                returnList.add(entity);
            }
        }
        return returnList;
    }
    
}
