package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.sap.StandardPriceService;
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

    private final StandardPriceService standardPriceService;

    private final SapMaterialService sapMaterialService;

    private final ModelMapper modelMapper;

    @PersistenceUnit
    private EntityManagerFactory emFactory;
    
    
    @Autowired
    public PriceRowServiceImpl(PriceRowRepository priceRowRepository,
                               MaterialService materialService,
                               MaterialPriceService materialPriceService, EntityManagerFactory emFactory,
                               StandardPriceService standardPriceService, SapMaterialService sapMaterialService,
                               @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        this.repository = priceRowRepository;
        this.materialPriceService = materialPriceService;
        this.materialService = materialService;
        this.emFactory = emFactory;
        this.standardPriceService = standardPriceService;
        this.sapMaterialService = sapMaterialService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice,
                                  Map<String, Map<String, Map<String, Discount>>> discountMap) {
        return saveAll(priceRowList, salesOrg, salesOffice, null, discountMap);
    }
    
    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice, String zone,
                                  Map<String, Map<String, Map<String, Discount>>> discountMap) {
        List<PriceRow> returnList = new ArrayList<>();
        for (PriceRow materialPriceRow : priceRowList) {
            PriceRow entity = save(materialPriceRow, salesOrg, salesOffice, zone, discountMap);

            returnList.add(entity);
        }
        
        log.debug("Collected {} amount of PriceRows", returnList.size());
        return returnList;
    }

    private PriceRow save(PriceRow materialPriceRow, String salesOrg, String salesOffice, String zone,
                          Map<String, Map<String, Map<String, Discount>>> discountMap) {
        log.debug("Price Row: {}", materialPriceRow);

        PriceRow entity = new PriceRow();

        if(materialPriceRow.getId() != null) {
            log.debug("Getting existing PriceRow");
            Optional<PriceRow> optPriceRow = repository.findById(materialPriceRow.getId());

            if(optPriceRow.isPresent()) {
                entity = optPriceRow.get();
            }
        }

        entity.setCustomerPrice(materialPriceRow.getCustomerPrice());
        entity.setDiscountPct(materialPriceRow.getDiscountPct());
        entity.setShowPriceInOffer(materialPriceRow.getShowPriceInOffer());
        entity.setManualPrice(materialPriceRow.getManualPrice());

        calculateDiscountPrice(entity, salesOrg, salesOffice, discountMap);

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

            EntityManager em = emFactory.createEntityManager();
            log.debug("Is material attached: {}", em.contains(material));

            if(material != null && material.getId() == null) {

                em.getTransaction().begin();
                var materials = em.createNamedQuery("findMaterialByMaterialNumber").setParameter("materialNumber", material.getMaterialNumber()).getResultList();
                em.getTransaction().commit();
                em.close();

                if(materials != null && materials.size() > 0) {
                    Material persistedMaterial = (Material) materials.get(0);
                    log.debug("Got Material: {}", persistedMaterial);
                    updateMaterial(persistedMaterial, material);

                    MaterialPrice persistedMaterialPrice = materialPriceService.findByMaterialNumber(persistedMaterial.getMaterialNumber());

                    if(persistedMaterialPrice != null) {
                        updateMaterialPrice(persistedMaterialPrice, material.getMaterialStandardPrice());
                        persistedMaterial.setMaterialStandardPrice(persistedMaterialPrice);
                    } else {
                        log.debug("No MaterialPrice for Material, getting standard price for material: {}", material.getMaterialNumber());
                        MaterialPrice stdPrice = standardPriceService.getStandardPriceForMaterial(material.getMaterialNumber(), salesOrg, salesOffice);
                        persistedMaterial.setMaterialStandardPrice(stdPrice);
                        persistedMaterial.setPricingUnit(stdPrice.getPricingUnit());
                        persistedMaterial.setQuantumUnit(stdPrice.getQuantumUnit());
                    }

                    persistedMaterial = materialService.save(persistedMaterial);

                    entity.setMaterial(persistedMaterial);
                } else {
                    log.debug("New Material to create with material number: {}", material.getMaterialNumber());
                    MaterialDTO sapMaterial = sapMaterialService.getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(material.getMaterialNumber(), salesOrg, salesOffice, zone);

                    if(sapMaterial != null) {
                        log.debug("Mapping MaterialDTO: {}", sapMaterial);
                        Material fromSap = modelMapper.map(sapMaterial, Material.class);

                        if(fromSap.getPricingUnit() == null || StringUtils.isBlank(fromSap.getQuantumUnit())) {
                            MaterialPrice stdPrice = standardPriceService.getStandardPriceForMaterial(material.getMaterialNumber(), salesOrg, salesOffice);

                            if(stdPrice != null) {
                                fromSap.setQuantumUnit(stdPrice.getQuantumUnit());
                                fromSap.setPricingUnit(stdPrice.getPricingUnit());
                            }
                        }

                        log.debug("Mapping result: {}", fromSap);

                        material = materialService.save(fromSap);
                    } else {
                        log.debug("Could not find material {} for salesorg {}", material.getMaterialNumber(), salesOrg);
                    }

                    entity.setMaterial(material);
                }

            } else if(material != null) {
                log.debug("Adding material to PriceRow: {}", material.getMaterialNumber());
                entity.setMaterial(material);
            }

        }

        if(materialPriceRow.hasCombinedMaterials()) {
            List<PriceRow> combinedMaterialPriceRows = new ArrayList<>();

            for(int j = 0; j < materialPriceRow.getCombinedMaterials().size(); j++) {
                PriceRow combinedMaterial = this.save(materialPriceRow.getCombinedMaterials().get(j), salesOrg, salesOffice, zone, discountMap);
                combinedMaterialPriceRows.add(combinedMaterial);
            }

            entity.setCombinedMaterials(combinedMaterialPriceRows);
        }

        return repository.save(entity);
    }

    private void calculateDiscountPrice(PriceRow entity, String salesOrg, String salesOffice, Map<String, Map<String, Map<String, Discount>>> discountMap) {
        if(entity.getDiscountedPrice() == null) {
            if(entity.getDiscountLevel() != null) {
                Discount discount = discountMap.get(salesOrg).get(salesOffice).get(entity.getMaterial().getMaterialNumber());

                if(discount != null && !discount.getDiscountLevels().isEmpty()) {
                    DiscountLevel dl = discount.getDiscountLevels().get(entity.getDiscountLevel());
                    log.debug("Getting discount for material {}, with discount level {}");

                    if(dl.getDiscount() < 0.0) {
                        entity.setDiscountedPrice(entity.getStandardPrice() + dl.getDiscount());
                    } else {
                        entity.setDiscountedPrice(entity.getStandardPrice() - dl.getDiscount());
                    }
                    entity.setDiscountedPrice(entity.getStandardPrice() - dl.getDiscount());
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
        log.debug("Material has no ID, search by material number: {}", material.getMaterialNumber());
        Material byMaterialNumber = materialService.findByMaterialNumber(material.getMaterialNumber());

        if(byMaterialNumber != null) {
            return byMaterialNumber;
        }

        return material;
    }

    private void updateMaterial(Material to, Material from) {
        log.debug("To: {}, from: {}", to, from);
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
        to.setStandardPrice(from.getStandardPrice());
        to.setValidFrom(from.getValidFrom());
        to.setValidTo(from.getValidTo());
    }
    
}
