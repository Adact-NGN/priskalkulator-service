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

        Material entity;
        if(StringUtils.isNotBlank(material.getDeviceType())) {
            log.debug("Searching for material with number: {} and device type: {}", material.getMaterialNumber(), material.getDeviceType());
            entity = repository.findByMaterialNumberAndDeviceType(material.getMaterialNumber(), material.getDeviceType());
        } else {
            log.debug("Searching for material with number: {}", material.getMaterialNumber());
            entity = repository.findByMaterialNumber(material.getMaterialNumber());
        }
        log.debug("Found material: {}", entity);
        
        if(entity == null) {
            log.debug("Didn't find a material with the number: {}", material.getMaterialNumber());
            log.debug("Creating a new one...");
            entity = new Material();
            entity.setMaterialNumber(material.getMaterialNumber());
            entity.setDeviceType(material.getDeviceType());
        } else {
            log.debug("Found material {} ...", material.getMaterialNumber());
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

        MaterialPrice materialPriceEntity = getMaterialPrice(material);

        entity.setMaterialStandardPrice(materialPriceEntity);

        if(materialPriceEntity != null && !StringUtils.equals(entity.getDeviceType(), materialPriceEntity.getDeviceType())) {
            entity.setDeviceType(materialPriceEntity.getDeviceType());
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

    private MaterialPrice getMaterialPrice(Material material) {
        log.debug("Getting std price for material: {}", material.getMaterialNumber());
        Optional<MaterialPrice> materialPriceEntityOptional = materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        material.getSalesOrg(),
                        material.getSalesOffice(),
                        material.getMaterialNumber(),
                        material.getDeviceType(),
                        material.getSalesZone());

        if(materialPriceEntityOptional.isEmpty()) {
            log.debug("Std price for material not found.");
            return material.getMaterialStandardPrice();
        } else if(material.getMaterialStandardPrice() != null){
            return material.getMaterialStandardPrice();
        }
        return null;
    }

    @Override
    public List<Material> saveAll(List<Material> materialList) {
        List<Material> returnMaterials = new ArrayList<>();

        for (Material material : materialList) {
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

    @Override
    public Material findByMaterialNumberAndDeviceType(String material, String deviceType) {
        return repository.findByMaterialNumberAndDeviceType(material, deviceType);
    }
}
