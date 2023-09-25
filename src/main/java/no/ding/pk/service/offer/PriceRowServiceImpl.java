package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class PriceRowServiceImpl implements PriceRowService {
    
    private static final Logger log = LoggerFactory.getLogger(PriceRowServiceImpl.class);
    
    private final PriceRowRepository repository;

    private final MaterialService materialService;
    
    private final MaterialPriceService materialPriceService;

    private final SapMaterialService sapMaterialService;

    private final ModelMapper modelMapper;

    @PersistenceUnit
    private EntityManagerFactory emFactory;
    
    
    @Autowired
    public PriceRowServiceImpl(PriceRowRepository priceRowRepository,
                               MaterialService materialService,
                               MaterialPriceService materialPriceService,
                               EntityManagerFactory emFactory,
                               SapMaterialService sapMaterialService,
                               @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        this.repository = priceRowRepository;
        this.materialPriceService = materialPriceService;
        this.materialService = materialService;
        this.emFactory = emFactory;
        this.sapMaterialService = sapMaterialService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice,
                                  List<MaterialPrice> materialStdPrices,
                                  Map<String, Map<String, Map<String, Discount>>> discountMap) {
        return saveAll(priceRowList, salesOrg, salesOffice, null, materialStdPrices, discountMap);
    }
    
    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice, String zone,
                                  List<MaterialPrice> materialStdPrices,
                                  Map<String, Map<String, Map<String, Discount>>> discountMap) {
        List<PriceRow> returnList = new ArrayList<>();
        for (PriceRow materialPriceRow : priceRowList) {
            MaterialPrice materialPrice = getMaterialPriceForMaterial(materialPriceRow.getMaterial(), materialStdPrices);
            log.debug("Found standard price for material: {}: {}", materialPriceRow.getMaterial().getMaterialNumber(), materialPrice);
            PriceRow entity = save(materialPriceRow, salesOrg, salesOffice, zone, materialPrice, discountMap);

            returnList.add(entity);
        }
        
        log.debug("Collected {} amount of PriceRows", returnList.size());
        return returnList;
    }

    private MaterialPrice getMaterialPriceForMaterial(Material material, List<MaterialPrice> materialStdPrices) {
        if(materialStdPrices == null || materialStdPrices.isEmpty()) {
            return null;
        }
        if(StringUtils.isNotBlank(material.getDeviceType())) {
            return materialStdPrices.stream().filter(materialPrice ->
                    materialPrice.getMaterialNumber().equals(material.getMaterialNumber()) && materialPrice.getDeviceType().equals(material.getDeviceType())).findFirst().orElse(null);
        }
        return materialStdPrices.stream().filter(materialPrice -> materialPrice.getMaterialNumber().equals(material.getMaterialNumber())).findFirst().orElse(null);
    }

    private PriceRow save(PriceRow materialPriceRow, String salesOrg, String salesOffice, String zone,
                          MaterialPrice materialPrice,
                          Map<String, Map<String, Map<String, Discount>>> discountMap) {
        log.debug("Price Row: {}", materialPriceRow);
        log.debug("Material Price: {}", materialPrice);

        PriceRow entity = new PriceRow();

        if(materialPriceRow.getId() != null) {
            log.debug("Getting existing PriceRow");
            Optional<PriceRow> optPriceRow = repository.findById(materialPriceRow.getId());

            if(optPriceRow.isPresent()) {
                entity = optPriceRow.get();
            }
        }

        entity.setCustomerPrice(materialPriceRow.getCustomerPrice());
        entity.setDiscountLevelPct(materialPriceRow.getDiscountLevelPct());
        entity.setShowPriceInOffer(materialPriceRow.getShowPriceInOffer());
        entity.setManualPrice(materialPriceRow.getManualPrice());

        entity.setDiscountLevel(materialPriceRow.getDiscountLevel());
        entity.setDiscountLevelPrice(materialPriceRow.getDiscountLevelPrice());
        entity.setStandardPrice(materialPriceRow.getStandardPrice());
        entity.setAmount(materialPriceRow.getAmount());
        entity.setPriceIncMva(materialPriceRow.getPriceIncMva());
        entity.setCategoryId(materialPriceRow.getCategoryId());
        entity.setCategoryDescription(materialPriceRow.getCategoryDescription());
        entity.setSubCategoryId(materialPriceRow.getSubCategoryId());
        entity.setSubCategoryDescription(materialPriceRow.getSubCategoryDescription());
        entity.setClassId(materialPriceRow.getClassId());
        entity.setClassDescription(materialPriceRow.getClassDescription());
        entity.setNeedsApproval(materialPriceRow.getNeedsApproval());
        entity.setApproved(materialPriceRow.getApproved());

        entity = repository.save(entity);

        if(materialPriceRow.getMaterial() != null) {
            Material material = getMaterial(materialPriceRow.getMaterial());
            log.debug("PriceRow->Material: {}", material);

            if(material.getId() == null) {

                List materials = getMaterials(material);

                if(materials != null && materials.size() > 0) {
                    Material persistedMaterial = (Material) materials.get(0);
                    log.debug("Got Material: {}", persistedMaterial);
                    updateMaterial(persistedMaterial, material);

                    MaterialPrice persistedMaterialPrice = materialPriceService.findByMaterialNumber(persistedMaterial.getMaterialNumber());

                    if(persistedMaterialPrice != null) {
                        updateMaterialPrice(persistedMaterialPrice, material.getMaterialStandardPrice());
                        persistedMaterial.setMaterialStandardPrice(persistedMaterialPrice);

                        if(materialPrice != null) {
                            persistedMaterial.setPricingUnit(materialPrice.getPricingUnit());
                            persistedMaterial.setQuantumUnit(materialPrice.getQuantumUnit());
                            persistedMaterial.setDeviceType(materialPrice.getDeviceType());
                        }

                    } else {
                        log.debug("No MaterialPrice for Material, getting standard price for material: {}", material.getMaterialNumber());
                        persistedMaterial.setMaterialStandardPrice(materialPrice);
                        persistedMaterial.setPricingUnit(materialPrice.getPricingUnit());
                        persistedMaterial.setQuantumUnit(materialPrice.getQuantumUnit());
                        persistedMaterial.setDeviceType(materialPrice.getDeviceType());

                        materialPriceRow.setStandardPrice(materialPrice.getStandardPrice());
                    }

                    persistedMaterial = materialService.save(persistedMaterial);

                    entity.setMaterial(persistedMaterial);
                } else {
                    log.debug("New Material to create with material number: {}", material.getMaterialNumber());
                    MaterialDTO sapMaterial = sapMaterialService.getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(material.getMaterialNumber(), salesOrg, salesOffice, zone);

                    if(sapMaterial != null) {
                        log.debug("Mapping MaterialDTO: {}", sapMaterial);
                        Material fromSap = modelMapper.map(sapMaterial, Material.class);

                        if(StringUtils.isBlank(fromSap.getDeviceType()) || fromSap.getPricingUnit() == null || StringUtils.isBlank(fromSap.getQuantumUnit())) {
                            updateMaterialWithMaterialPriceValues(materialPriceRow, materialPrice, fromSap);
                        }

                        log.debug("Mapping result: {}", fromSap);

                        material = materialService.save(fromSap);
                    } else {
                        log.debug("Could not find material {} for salesorg {}", material.getMaterialNumber(), salesOrg);
                    }

                    entity.setMaterial(material);
                }

            } else {
                log.debug("Adding material to PriceRow: {}", material.getMaterialNumber());

                updateMaterialWithMaterialPriceValues(entity, materialPrice, material);

                entity.setMaterial(material);
            }
        }

        if(entity.getManualPrice() != null) {
            entity.setDiscountedPrice(entity.getManualPrice());

            Integer discountLevel = getEquivalentDiscountLevel(entity, salesOrg, salesOffice, discountMap);
            if(discountLevel != null) {
                entity.setDiscountLevel(discountLevel);
            } else {
                log.info("Could not get discount level equivalent for manual price.");
            }
        } else {
            calculateDiscountPrice(entity, salesOrg, salesOffice, discountMap);
        }

        if(materialPriceRow.hasCombinedMaterials()) {
            List<PriceRow> combinedMaterialPriceRows = new ArrayList<>();

            for(int j = 0; j < materialPriceRow.getCombinedMaterials().size(); j++) {
                PriceRow combinedMaterial = this.save(materialPriceRow.getCombinedMaterials().get(j), salesOrg, salesOffice, zone, materialPrice, discountMap);
                combinedMaterialPriceRows.add(combinedMaterial);
            }

            entity.setCombinedMaterials(combinedMaterialPriceRows);
        }

        return repository.save(entity);
    }

    private void updateMaterialWithMaterialPriceValues(PriceRow materialPriceRow, MaterialPrice materialPrice, Material material) {
        if(materialPrice != null) {
            material.setDeviceType(materialPrice.getDeviceType());
            material.setQuantumUnit(materialPrice.getQuantumUnit());
            material.setPricingUnit(materialPrice.getPricingUnit());

            materialPriceRow.setStandardPrice(materialPrice.getStandardPrice());
            if(material.getMaterialStandardPrice() == null) {
                material.setMaterialStandardPrice(materialPrice);
            } else {
                modelMapper.map(materialPrice, material.getMaterialStandardPrice());
            }

        } else {
            log.debug("No material prices found.");
        }
    }

    private Integer getEquivalentDiscountLevel(PriceRow entity, String salesOrg, String salesOffice, Map<String, Map<String, Map<String, Discount>>> discountMap) {
        Discount discount = getDiscountLevel(salesOrg, salesOffice, entity.getMaterial().getMaterialNumber(), discountMap);

        if(discount == null) {
            log.debug("No discount level found for {}", entity.getMaterial().getMaterialNumber());
            return null;
        }

        if(entity.getMaterial() == null || entity.getMaterial().getMaterialStandardPrice() == null || entity.getMaterial().getMaterialStandardPrice().getStandardPrice() == null) {
            log.debug("Could not get material information for calculating discount level: {}", entity);
            return null;
        }

        Double standardPrice = entity.getMaterial().getMaterialStandardPrice().getStandardPrice();

        Double manualPrice = entity.getManualPrice();

        Integer currentLevel = 1;
        for (DiscountLevel discountLevel : discount.getDiscountLevels()) {
            Double tempDiscount = standardPrice - discountLevel.getDiscount();

            if(manualPrice < tempDiscount) {
                currentLevel++;
            } else {
                break;
            }
        }

        return currentLevel;
    }

    private Discount getDiscountLevel(String salesOrg, String salesOffice, String materialNumber, Map<String, Map<String, Map<String, Discount>>> discountMap) {
        if(discountMap.containsKey(salesOrg)) {
            Map<String, Map<String, Discount>> salesOrgDiscountMap = discountMap.get(salesOrg);
            if(salesOrgDiscountMap != null && salesOrgDiscountMap.containsKey(salesOffice)) {
                Map<String, Discount> salesOfficeDiscountMap = salesOrgDiscountMap.get(salesOffice);

                if(salesOfficeDiscountMap.containsKey(materialNumber)) {
                    return salesOfficeDiscountMap.get(materialNumber);
                }
            }
        }
        return null;
    }

    private List getMaterials(Material material) {
        EntityManager em = emFactory.createEntityManager();
        log.debug("Is material attached: {}", em.contains(material));

        em.getTransaction().begin();
        List materials;

        if(StringUtils.isNotBlank(material.getDeviceType())) {
            materials = em.createNamedQuery("findMaterialByMaterialNumberAndDeviceType").setParameter("materialNumber", material.getMaterialNumber()).setParameter("deviceType", material.getDeviceType()).getResultList();
        } else {
            materials = em.createNamedQuery("findMaterialByMaterialNumber").setParameter("materialNumber", material.getMaterialNumber()).getResultList();
        }
        em.getTransaction().commit();
        em.close();
        return materials;
    }

    private void calculateDiscountPrice(PriceRow entity, String salesOrg, String salesOffice, Map<String, Map<String, Map<String, Discount>>> discountMap) {
        if(discountMap == null || discountMap.isEmpty()) {
            log.debug("No discount map provided for material: {}", entity.getMaterial().getMaterialNumber());
            return;
        }
        if(entity.getDiscountedPrice() == null) {
            if(entity.getDiscountLevel() != null) {
                Discount discount = discountMap.get(salesOrg).get(salesOffice).get(entity.getMaterial().getMaterialNumber());

                if(discount != null && !discount.getDiscountLevels().isEmpty()) {
                    Optional<DiscountLevel> optionalDl = discount.getDiscountLevels().stream().filter(dlevel -> dlevel.getLevel() == entity.getDiscountLevel()).findFirst();

                    if(optionalDl.isEmpty()) {
                        log.debug("Discount was found, but no discount level with value {} was found.", entity.getDiscountLevel());

                        return;
                    }

                    DiscountLevel dl = optionalDl.get();

                    log.debug("Getting discount for material {}, with discount level {}, with discount {}", entity.getMaterial().getMaterialNumber(), dl.getLevel(), dl.getDiscount());

                    Double discountLevelPct = dl.getPctDiscount();
                    Double discountLevelPrice = dl.getDiscount();

                    if(entity.getStandardPrice() == null || entity.getStandardPrice() == 0.0) {
                        discountLevelPct = 0.0;
                        discountLevelPrice = 0.0;
                    } else if(discountLevelPct == null && discountLevelPrice != null) {
                        if(discountLevelPrice < 0.0) {
                            discountLevelPct = ((discountLevelPrice * -1.0) * 100) / entity.getStandardPrice();
                        } else {
                            discountLevelPct = (discountLevelPrice * 100) / entity.getStandardPrice();
                        }
                    }
                    entity.setDiscountLevelPct(discountLevelPct);
                    entity.setDiscountLevelPrice(discountLevelPrice);

                    if(dl.getDiscount() < 0.0) {
                        entity.setDiscountedPrice(entity.getStandardPrice() + dl.getDiscount());
                    } else {
                        entity.setDiscountedPrice(entity.getStandardPrice() - dl.getDiscount());
                    }
                } else {
                    log.debug("No discount found for material {} for sales office {} in sales org {}",
                            entity.getMaterial().getMaterialNumber(), salesOffice, salesOrg);
                }
            } else {
                log.debug("No discount level set for price row.");
            }
        } else {
            log.debug("Discounted price already set.");
        }
    }

    private Material getMaterial(Material material) {

        if(material.getId() != null) {
            log.debug("Material has ID: {}", material.getId());
            return materialService.findById(material.getId()).orElse(material);
        }

        Material byMaterialNumber;
        if(StringUtils.isNotBlank(material.getDeviceType())) {
            log.debug("Material has no ID, search by material number: {} and Device type: {}", material.getMaterialNumber(), material.getDeviceType());
            byMaterialNumber = materialService.findByMaterialNumberAndDeviceType(material.getMaterialNumber(), material.getDeviceType());
        } else {
            log.debug("Material has no ID, search by material number: {}", material.getMaterialNumber());
            byMaterialNumber = materialService.findByMaterialNumber(material.getMaterialNumber());
        }

        if(byMaterialNumber != null) {
            return byMaterialNumber;
        }

        return material;
    }

    private void updateMaterial(Material to, Material from) {
        log.debug("To: {}, from: {}", to, from);
//        modelMapper.map(from, to);
        to.setDesignation(from.getDesignation());
        to.setMaterialGroup(from.getMaterialGroup());
        to.setMaterialGroupDesignation(from.getMaterialGroupDesignation());
        to.setMaterialType(from.getMaterialType());
        to.setMaterialTypeDescription(from.getMaterialTypeDescription());
        to.setDeviceType(from.getDeviceType());

        to.setCurrency(from.getCurrency());
        to.setPricingUnit(from.getPricingUnit());
        to.setQuantumUnit(from.getQuantumUnit());
        to.setSalesZone(from.getSalesZone());

        to.setCategoryId(from.getCategoryId());
        to.setCategoryDescription(from.getCategoryDescription());
        to.setSubCategoryId(from.getSubCategoryId());
        to.setSubCategoryDescription(from.getSubCategoryDescription());
        to.setClassId(from.getClassId());
        to.setClassDescription(from.getClassDescription());
    }
    
    private void updateMaterialPrice(MaterialPrice to, MaterialPrice from) {
        log.debug("To: {}, from: {}", to, from);
//        modelMapper.map(from, to);
        to.setStandardPrice(from.getStandardPrice());
        to.setValidFrom(from.getValidFrom());
        to.setValidTo(from.getValidTo());
        to.setDeviceType(from.getDeviceType());
        to.setPricingUnit(from.getPricingUnit());
        to.setMaterialNumber(from.getMaterialNumber());
        to.setQuantumUnit(from.getQuantumUnit());
    }
    
}
