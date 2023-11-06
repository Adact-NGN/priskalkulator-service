package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.ding.pk.repository.specifications.MaterialSpecifications.*;

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
    public List<Material> findAll() {
        return repository.findAll();
    }

    @Override
    public Material save(Material material) {
        
        if(material.getMaterialNumber() == null) {
            throw new RuntimeException("Received material without a material number.");
        }

        String deviceType = StringUtils.isNotBlank(material.getDeviceType()) ? material.getDeviceType() : null;
        log.debug("Searching for material with number: {} and device type: {}", material.getMaterialNumber(), deviceType);
        Optional<Material> optionalEntity = repository.findByMaterialNumberAndDeviceType(material.getMaterialNumber(), deviceType);

        log.debug("Found material: {}", optionalEntity.isPresent());

        Material entity;
        if(optionalEntity.isEmpty()) {
            log.debug("Didn't find a material with the number: {}", material.getMaterialNumber());
            log.debug("Creating a new one...");
            entity = new Material();
            entity.setMaterialNumber(material.getMaterialNumber());
            entity.setDeviceType(material.getDeviceType());
        } else {
            log.debug("Found material {} ...", material.getMaterialNumber());
            entity = optionalEntity.get();
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
        
        if(StringUtils.isNotBlank(material.getDeviceType()) && !StringUtils.equals(entity.getDeviceType(), material.getDeviceType())) {
            entity.setDeviceType(material.getDeviceType());
        }

        if(!StringUtils.equals(entity.getCurrency(), material.getCurrency())) {
            entity.setCurrency(material.getCurrency());
        }
        
        if(!StringUtils.equals(entity.getQuantumUnit(), material.getQuantumUnit())) {
            entity.setQuantumUnit(material.getQuantumUnit());
        }

        if(entity.getScaleQuantum() != null && !entity.getScaleQuantum().equals(material.getScaleQuantum())) {
            entity.setScaleQuantum(material.getScaleQuantum());
        }
        
        return repository.save(entity);
    }

    private MaterialPrice getMaterialPrice(String salesOrg, String salesOffice, Material material, String salesZone) {
        log.debug("Getting std price for material: {}", material.getMaterialNumber());
        Optional<MaterialPrice> materialPriceEntityOptional = materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        salesOrg,
                        salesOffice,
                        material.getMaterialNumber(),
                        material.getDeviceType(),
                        salesZone);

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
    public Optional<Material> findByMaterialNumber(String materialNumber) {
        return repository.findByMaterialNumber(materialNumber);
    }

    @Override
    public Optional<Material> findByMaterialNumberAndDeviceType(String material, String deviceType) {
        log.debug("Finding material: {}, {}", material, deviceType);
        return repository.findByMaterialNumberAndDeviceType(material, deviceType);
    }

    @Override
    public Optional<Material> findBy(String salesOrg, String salesOffice, String materialNumber, String deviceType, String zone) {
        List<Material> materials = repository.findAll(Specification.where(withSalesOrg(salesOrg))
                .and(withSalesOffice(salesOffice))
                .and(withMaterialNumber(materialNumber))
                .and(withDeviceType(deviceType))
                .and(withZone(zone)));

        if(materials.isEmpty()) {
            return Optional.empty();
        }

        if(materials.size() > 1) {
            log.debug("Found more than one material.");
            throw new RuntimeException("Found more than one material.");
        }

        return Optional.ofNullable(materials.get(0));
    }

    @Override
    public List<Material> findBy(String materialNumber, String deviceType, String salesZone) {
        return repository.findAll(Specification.where(withMaterialNumber(materialNumber)).and(withDeviceType(deviceType)).and(withZone(salesZone)));
    }
}
