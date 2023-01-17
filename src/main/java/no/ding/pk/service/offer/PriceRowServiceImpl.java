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
import no.ding.pk.repository.offer.PriceRowRepository;

@Transactional
@Service
public class PriceRowServiceImpl implements PriceRowService {
    
    private static Logger log = LoggerFactory.getLogger(PriceRowServiceImpl.class);
    
    private PriceRowRepository repository;
    
    private MaterialService materialService;
    
    
    @Autowired
    public PriceRowServiceImpl(PriceRowRepository priceRowRepository, MaterialService materialService) {
        this.repository = priceRowRepository;
        this.materialService = materialService;
    }
    
    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList) {
        List<PriceRow> returnList = new ArrayList<>();
        for(int i = 0; i < priceRowList.size(); i++) {
            PriceRow materialPriceRow = priceRowList.get(i);

            log.debug("Price Row: {}", materialPriceRow);
            
            PriceRow entity = new PriceRow();
            
            if(materialPriceRow.getId() != null) {
                log.debug("Creating new PriceRow");
                Optional<PriceRow> optPriceRow = repository.findById(materialPriceRow.getId());
                
                if(optPriceRow.isPresent()) {
                    entity = optPriceRow.get();
                }
            }
            
//            entity.setCustomerPrice(materialPriceRow.getCustomerPrice());
//            entity.setDiscountPct(materialPriceRow.getDiscountPct());
//            entity.setShowPriceInOffer(materialPriceRow.getShowPriceInOffer());
//            entity.setManualPrice(materialPriceRow.getManualPrice());
//            entity.setDiscountLevel(materialPriceRow.getDiscountLevel());
//            entity.setDiscountLevelPrice(materialPriceRow.getDiscountLevelPrice());
//            entity.setStandardPrice(materialPriceRow.getStandardPrice());
//            entity.setAmount(materialPriceRow.getAmount());
//            entity.setPriceIncMva(materialPriceRow.getPriceIncMva());
            
            if(materialPriceRow.getMaterial() != null) {
                log.debug("PriceRow->Material: {}", materialPriceRow.getMaterial());
                entity.setMaterial(materialPriceRow.getMaterial());


//                Material material = materialService.save(materialPriceRow.getMaterial());
//
//                log.debug(String.format("Id from persisted operation: %d", material.getId()));
//                Optional<Material> optMaterial = materialService.findById(material.getId());
//
//                log.debug("Returned material: " + optMaterial.get());
//                if(optMaterial.isPresent()) {
//                    entity.setMaterial(optMaterial.get());
//                }
            }
            
//            entity = repository.save(entity);
            
            returnList.add(entity);
        }
        
        log.debug("Persisted {} amount of PriceRows", returnList.size());
        return returnList;
    }
    
}
