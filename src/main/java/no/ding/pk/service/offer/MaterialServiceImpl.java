package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialRepository;
import org.apache.commons.lang3.StringUtils;
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
public class MaterialServiceImpl implements MaterialService {
    
    private static final Logger log = LoggerFactory.getLogger(MaterialServiceImpl.class);
    
    private final MaterialRepository repository;
    private final MaterialPriceService materialPriceService;
    
    @Autowired
    public MaterialServiceImpl(MaterialRepository repository, MaterialPriceService materialPriceService) {
        this.repository = repository;
        this.materialPriceService = materialPriceService;
    }
    
    @Override
    public Material save(Material material) {
        
        if(material.getMaterialNumber() == null) {
            throw new RuntimeException("Received material without a material number.");
        }
        
        log.debug("Searching for material with number: {}", material.getMaterialNumber());
        Material entity = repository.findByMaterialNumber(material.getMaterialNumber());
        log.debug("Found material: {}", entity);
        
        if(entity == null) {
            log.debug("Didn't find a material with the number: {}", material.getMaterialNumber());
            log.debug("Creating a new one...");
            entity = new Material();
            entity.setMaterialNumber(material.getMaterialNumber());
        } else {
            log.debug("Found material...");
        }

        if(entity.getPricingUnit() != null && !entity.getPricingUnit().equals(material.getPricingUnit())) {
            entity.setPricingUnit(material.getPricingUnit());
        } else if(material.getPricingUnit() != null) {
            entity.setPricingUnit(material.getPricingUnit());
        }
        
        if(!StringUtils.equals(entity.getDesignation(), material.getDesignation())) {
            entity.setDesignation(material.getDesignation());
        }

        if(!StringUtils.equals(entity.getMaterialGroup(), material.getMaterialGroup())) {
            entity.setMaterialGroup(material.getMaterialGroup());
        }

        if(!StringUtils.equals(entity.getMaterialGroupDesignation(), material.getMaterialGroupDesignation())) {
            entity.setMaterialGroupDesignation(material.getMaterialGroupDesignation());
        }

        if(!StringUtils.equals(entity.getMaterialType(), material.getMaterialType())) {
            entity.setMaterialType(material.getMaterialType());
        }

        if(!StringUtils.equals(entity.getMaterialTypeDescription(), material.getMaterialTypeDescription())) {
            entity.setMaterialTypeDescription(material.getMaterialTypeDescription());
        }
        
        if(!StringUtils.equals(entity.getDeviceType(), material.getDesignation())) {
            entity.setDeviceType(material.getDeviceType());
        }
        
        MaterialPrice materialPriceEntity = materialPriceService.findByMaterialNumber(material.getMaterialNumber());
        
        if(materialPriceEntity == null) {
            materialPriceEntity = material.getMaterialStandardPrice();
        } else if(material.getMaterialStandardPrice() != null){
            materialPriceEntity.copy(material.getMaterialStandardPrice());
        }
        
        if(materialPriceEntity != null) {
            entity.setMaterialStandardPrice(materialPriceEntity);
        }

        if(!StringUtils.equals(entity.getCurrency(), material.getCurrency())) {
            entity.setCurrency(material.getCurrency());
        }
        
        if(entity.getPricingUnit() != null && !entity.getPricingUnit().equals(material.getPricingUnit())) {
            entity.setPricingUnit(material.getPricingUnit());
        }
        
        if(!StringUtils.equals(entity.getQuantumUnit(), material.getQuantumUnit())) {
            entity.setQuantumUnit(material.getQuantumUnit());
        }

        if(entity.getScaleQuantum() != null && !entity.getScaleQuantum().equals(material.getScaleQuantum())) {
            entity.setScaleQuantum(material.getScaleQuantum());
        }
        
        if(!StringUtils.equals(entity.getSalesZone(), material.getSalesZone())) {
            entity.setSalesZone(material.getSalesZone());
        }
        
        return repository.save(entity);
    }
    
    @Override
    public List<Material> saveAll(List<Material> materialList) {
        List<Material> returnMaterials = new ArrayList<>();
        
        for(int i = 0; i < materialList.size(); i++) {
            Material material = materialList.get(i);
            
            material = save(material);
            
            returnMaterials.add(material);
        }
        
        return returnMaterials;
    }
    
    @Override
    public Optional<Material> findById(Long id) {
        return repository.findById(id);
    }
    
    @Override
    public Material findByMaterialNumber(String materialNumber) {
        return repository.findByMaterialNumber(materialNumber);
    }
}
