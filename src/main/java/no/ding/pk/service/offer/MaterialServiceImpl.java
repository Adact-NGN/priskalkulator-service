package no.ding.pk.service.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialRepository;

@Transactional
@Service
public class MaterialServiceImpl implements MaterialService {

    private static final Logger log = LoggerFactory.getLogger(MaterialServiceImpl.class);
    
    private MaterialRepository repository;
    private MaterialPriceService materialPriceService;
    
    @Autowired
    public MaterialServiceImpl(MaterialRepository repository, MaterialPriceService materialPriceService) {
        this.repository = repository;
        this.materialPriceService = materialPriceService;
    }
    
    @Override
    public Material save(Material material) {

        log.debug("Searching for material with number: {}", material.getMaterialNumber());
        List<Material> materialList = repository.findAll();
        Material entity = repository.findByMaterialNumber(material.getMaterialNumber());
        
        if(entity == null) {
            entity = new Material();
        }

        if(material.getMaterialNumber() == null) {
            throw new RuntimeException("Received material without a material number.");
        }
        
        entity.setMaterialNumber(material.getMaterialNumber());
        entity.setDesignation(material.getDesignation());
        entity.setDeviceType(material.getDeviceType());

        MaterialPrice materialPriceEntity = materialPriceService.findByMaterialNumber(material.getMaterialNumber());

        if(materialPriceEntity == null) {
            materialPriceEntity = material.getMaterialStandardPrice();
        } else if(material.getMaterialStandardPrice() != null){
            materialPriceEntity.copy(material.getMaterialStandardPrice());
        }

        if(materialPriceEntity != null) {
            entity.setMaterialStandardPrice(materialPriceEntity);
        }

        entity.setPriceUnit(material.getPriceUnit());
        entity.setQuantumUnit(material.getQuantumUnit());
        entity.setSalesZone(material.getSalesZone());
        
        return repository.save(material);
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
