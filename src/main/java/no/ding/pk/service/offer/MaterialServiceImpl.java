package no.ding.pk.service.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialRepository;

@Transactional
@Service
public class MaterialServiceImpl implements MaterialService {
    
    private MaterialRepository repository;
    private MaterialPriceService materialPriceService;
    
    @Autowired
    public MaterialServiceImpl(MaterialRepository repository, MaterialPriceService materialPriceService) {
        this.repository = repository;
        this.materialPriceService = materialPriceService;
    }
    
    @Override
    public Material save(Material material) {
        
        MaterialPrice materialPrice = null;
        if(material.getMaterialStandardPrice() != null) {
            materialPrice = materialPriceService.save(material.getMaterialStandardPrice());
        }
        
        if(material.getMaterialNumber() == null) {
            throw new RuntimeException("Received material without a material number.");
        }
        
        Material entity = getMaterialByMaterialNumber(material.getMaterialNumber());
        
        if(entity == null && material.getId() != null) {
            Optional<Material> optMaterial = repository.findById(material.getId());
            
            if(optMaterial.isPresent()) {
                entity = optMaterial.get();
            }
        }
        
        entity.setMaterialNumber(material.getMaterialNumber());
        entity.setDesignation(material.getDesignation());
        entity.setDeviceType(material.getDeviceType());
        if(materialPrice != null && materialPrice.getId() != null) {
            entity.setMaterialStandardPrice(materialPrice);
        }
        entity.setPricingUnit(material.getPricingUnit());
        entity.setQuantumUnit(material.getQuantumUnit());
        
        return repository.save(material);
    }

    private Material getMaterialByMaterialNumber(String materialNumber) {
        Material material = repository.findByMaterialNumber(materialNumber);

        if(material == null) {
            return new Material();
        }

        return material;
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
