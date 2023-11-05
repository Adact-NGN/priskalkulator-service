package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.DiscountService;
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
import java.util.*;

@Transactional
@Service
public class PriceRowServiceImpl implements PriceRowService {
    
    private static final Logger log = LoggerFactory.getLogger(PriceRowServiceImpl.class);

    private final DiscountService discountService;

    private final PriceRowRepository repository;

    private final MaterialService materialService;
    
    private final MaterialPriceService materialPriceService;

    private final SapMaterialService sapMaterialService;

    private final ModelMapper modelMapper;

    @PersistenceUnit
    private EntityManagerFactory emFactory;
    
    
    @Autowired
    public PriceRowServiceImpl(
            DiscountService discountService,
            PriceRowRepository priceRowRepository,
            MaterialService materialService,
            MaterialPriceService materialPriceService,
            EntityManagerFactory emFactory,
            SapMaterialService sapMaterialService,
            @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        this.discountService = discountService;
        this.repository = priceRowRepository;
        this.materialPriceService = materialPriceService;
        this.materialService = materialService;
        this.emFactory = emFactory;
        this.sapMaterialService = sapMaterialService;
        this.modelMapper = modelMapper;
    }

    private static Integer getCurrentLevel(Discount discount, Double standardPrice, Double manualPrice) {
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
    
    private static Double getStandardPrice(PriceRow entity) {
        if(entity.getStandardPrice() != null) {
            return entity.getStandardPrice();
        }

        if (entity.getMaterial() != null && entity.getMaterial().getMaterialStandardPrice() != null && entity.getMaterial().getMaterialStandardPrice().getStandardPrice() != null) {
            return entity.getMaterial().getMaterialStandardPrice().getStandardPrice();
        }


        return null;
    }

    private static boolean isDiscountedPriceNotSetOrNotEqualToIncommingDiscountedPrice(PriceRow entity, Double currentDiscountedPrice) {
        return entity.getDiscountedPrice() == null || (currentDiscountedPrice != null && !entity.getDiscountedPrice().equals(currentDiscountedPrice));
    }

    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice,
                                  Map<String, MaterialPrice> materialStdPriceMap) {
        return saveAll(priceRowList, salesOrg, salesOffice, null, materialStdPriceMap);
    }

    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice, String zone,
                                  Map<String, MaterialPrice> materialStdPriceMap) {
        List<PriceRow> returnList = new ArrayList<>();
        for (PriceRow materialPriceRow : priceRowList) {
            MaterialPrice materialPrice = getMaterialPriceForMaterial(salesOrg, salesOffice, materialPriceRow.getMaterial(), materialStdPriceMap);
            log.debug("Found standard price for material: {}: {}, zone: {}", materialPriceRow.getMaterial().getMaterialNumber(), materialPrice, zone);
            PriceRow entity = save(materialPriceRow, salesOrg, salesOffice, zone, materialPrice);

            returnList.add(entity);
        }

        log.debug("Collected {} amount of PriceRows", returnList.size());
        return returnList;
    }

    private MaterialPrice getMaterialPriceForMaterial(String salesOrg, String salesOffice, Material material, Map<String, MaterialPrice> materialStdPriceMap) {
        if(materialStdPriceMap == null || materialStdPriceMap.isEmpty()) {
            return null;
        }

        StringBuilder lookUpKey = new StringBuilder();

        if(StringUtils.isNotBlank(salesOrg)) {
            lookUpKey.append(salesOrg);
        }

        if(StringUtils.isNotBlank(salesOffice)) {
            if(!lookUpKey.isEmpty()) {
                lookUpKey.append("_");
            }

            lookUpKey.append(salesOffice);
        }

        if(StringUtils.isNotBlank(material.getMaterialNumber())) {
            if(!lookUpKey.isEmpty()) {
                lookUpKey.append("_");
            }

            lookUpKey.append(material.getMaterialNumber());
        }

        if(StringUtils.isNotBlank(material.getDeviceType())) {
            if(!lookUpKey.isEmpty()) {
                lookUpKey.append("_");
            }
            lookUpKey.append(material.getDeviceType());
        }

        if(StringUtils.isNotBlank(material.getSalesZone())) {
            String salesZone = String.format("0%d", Integer.valueOf(material.getSalesZone())) ;
            lookUpKey.append("_").append(salesZone);
        }

        MaterialPrice materialPrice = materialStdPriceMap.getOrDefault(lookUpKey.toString(), null);

        log.debug("Got material price: {}", materialPrice);

        return materialPrice;
    }

    private PriceRow save(PriceRow materialPriceRow, String salesOrg, String salesOffice, String zone,
                          MaterialPrice materialPrice) {
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

        entity = repository.save(entity);

        if(materialPriceRow.getMaterial() != null) {

            Material material = getMaterial(salesOrg, salesOffice, materialPriceRow.getMaterial(), materialPrice);
            log.debug("PriceRow->Material: {}", material);

            if(material.getId() == null) {

                log.debug("New Material to create with material number: {}", material.getMaterialNumber());
                MaterialDTO sapMaterial = sapMaterialService.getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(material.getMaterialNumber(), salesOrg, salesOffice, zone);

                if(sapMaterial != null) {
                    log.debug("Mapping MaterialDTO: {}", sapMaterial);
                    Material fromSap = modelMapper.map(sapMaterial, Material.class);
                    log.debug("Mapping result: {}", fromSap);

                    updateMaterialWithMaterialPriceValues(materialPriceRow, materialPrice, fromSap);

                    material = materialService.save(fromSap);
                } else {
                    log.debug("Could not find material {} for salesorg {}", material.getMaterialNumber(), salesOrg);
                }

                entity.setMaterial(material);
            } else {
                log.debug("Adding material to PriceRow: {}", material.getMaterialNumber());

                updateMaterialWithMaterialPriceValues(entity, materialPrice, material);

                entity.setMaterial(material);
            }
        }

        if(materialPriceRow.getManualPrice() != null) {
            entity.setDiscountedPrice(entity.getManualPrice());

            Integer discountLevel = getEquivalentDiscountLevel(entity, salesOrg, salesOffice);
            if(discountLevel != null) {
                entity.setDiscountLevel(discountLevel);
            } else {
                log.info("Could not get discount level equivalent for manual price.");
            }
        } else if(materialPriceRow.getDiscountLevel() != null) {
            entity.setDiscountLevel(materialPriceRow.getDiscountLevel());
            entity.setDiscountLevelPct(materialPriceRow.getDiscountLevelPct());
            entity.setDiscountLevelPrice(materialPriceRow.getDiscountLevelPrice());
            Double currentDiscountedPrice = materialPriceRow.getDiscountedPrice();
            calculateDiscountPrice(entity, currentDiscountedPrice, salesOrg, salesOffice, zone);
        }

        if(materialPriceRow.hasCombinedMaterials()) {
            List<PriceRow> combinedMaterialPriceRows = new ArrayList<>();

            for(int j = 0; j < materialPriceRow.getCombinedMaterials().size(); j++) {
                PriceRow combinedMaterial = this.save(materialPriceRow.getCombinedMaterials().get(j), salesOrg, salesOffice, zone, materialPrice);
                combinedMaterialPriceRows.add(combinedMaterial);
            }

            entity.setCombinedMaterials(combinedMaterialPriceRows);
        }

        return repository.save(entity);
    }

    private void updateMaterialWithMaterialPriceValues(PriceRow materialPriceRow, MaterialPrice materialPrice, Material material) {
        if(materialPrice != null) {
            material.setSalesOrg(materialPrice.getSalesOrg());
            material.setSalesOffice(materialPrice.getSalesOffice());
            material.setSalesZone(materialPrice.getZone());
            material.setDeviceType(materialPrice.getDeviceType());
            material.setQuantumUnit(materialPrice.getQuantumUnit());
            material.setPricingUnit(materialPrice.getPricingUnit());

            materialPriceRow.setStandardPrice(materialPrice.getStandardPrice());

            Optional<MaterialPrice> existingMaterialPrice = materialPriceService
                    .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(material.getSalesOrg(),
                            material.getSalesOffice(),
                            material.getMaterialNumber(),
                            material.getDeviceType(),
                            material.getSalesZone());

            if(existingMaterialPrice.isPresent()) {
                MaterialPrice materialStandardPrice = existingMaterialPrice.get();

                // TODO: Should we update the price with the newest every time?
//                if(!materialStandardPrice.getStandardPrice().equals(materialPrice.getStandardPrice())) {
//                    materialStandardPrice.setStandardPrice(materialPrice.getStandardPrice());
//                }

                material.setMaterialStandardPrice(materialStandardPrice);
            } else {
                material.setSalesOrg(materialPrice.getSalesOrg());
                material.setSalesOffice(materialPrice.getSalesOffice());
                material.setDeviceType(materialPrice.getDeviceType());
                material.setSalesZone(materialPrice.getZone());

                material.setMaterialStandardPrice(materialPrice);
            }

        } else {
            log.debug("No material prices found.");
        }
    }

    private Integer getEquivalentDiscountLevel(PriceRow entity, String salesOrg, String salesOffice) {
        Discount discount = getDiscountLevel(salesOrg, salesOffice, entity.getMaterial().getMaterialNumber());

        if(discount == null) {
            log.debug("No discount level found for {}", entity.getMaterial().getMaterialNumber());
            return null;
        }

        Double standardPrice = getStandardPrice(entity);

        if(standardPrice == null) {
            log.debug("Could not get material information for calculating discount level: {}", entity);
            return null;
        }

        Double manualPrice = entity.getManualPrice();

        return getCurrentLevel(discount, standardPrice, manualPrice);
    }

    private Discount getDiscountLevel(String salesOrg, String salesOffice, String materialNumber) {
        List<Discount> discounts = discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(salesOrg, salesOffice, Collections.singletonList(materialNumber));

        if(discounts.isEmpty()) {
            return null;
        }
        return discounts.get(0);
    }

    private List getMaterials(Material material) {
        log.debug("Getting material from DB: {}", material);
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

    private void calculateDiscountPrice(PriceRow entity, Double currentDiscountedPrice, String salesOrg, String salesOffice, String zone) {
        if(isDiscountedPriceNotSetOrNotEqualToIncommingDiscountedPrice(entity, currentDiscountedPrice)) {
            if(entity.getDiscountLevel() != null) {
                Integer zoneAsInt = StringUtils.isNotBlank(zone) ? Integer.valueOf(zone) : null;
                List<DiscountLevel> discountLevels = discountService.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, salesOffice, entity.getMaterial().getMaterialNumber(), entity.getDiscountLevel(), zoneAsInt);

                if(discountLevels.isEmpty()) {
                    log.debug("Discount level was set, but no discount level with value {} was found for material {}.", entity.getDiscountLevel(), entity.getMaterial().getMaterialNumber());

                    return;
                }

                DiscountLevel dl = discountLevels.get(0);

                log.debug("Getting discount for material {}, with discount level {}, with discount {}", entity.getMaterial().getMaterialNumber(), dl.getLevel(), dl.getDiscount());

                Double discountLevelPct = dl.getPctDiscount();
                Double discountLevelPrice = dl.getDiscount();

                if(entity.getStandardPrice() == null || entity.getStandardPrice() == 0.0) {
                    discountLevelPct = 0.0;
                    discountLevelPrice = 0.0;
                } else if(discountLevelPct == null && discountLevelPrice != null) {
                    if(discountLevelPrice < 0.0) {
                        discountLevelPct = ((discountLevelPrice * -1.0)) / entity.getStandardPrice();
                    } else {
                        discountLevelPct = (discountLevelPrice) / entity.getStandardPrice();
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
                log.debug("No discount level set for price row.");
            }
        } else {
            log.debug("Discounted price already set.");
        }
    }

    private Material getMaterial(String salesOrg, String salesOffice, Material material, MaterialPrice materialPrice) {

        if(material.getId() != null) {
            log.debug("Material has ID: {}", material.getId());
            return materialService.findById(material.getId()).orElse(material);
        }

        Optional<Material> optionalByMaterialNumber;
        String deviceType = materialPrice != null ? materialPrice.getDeviceType() : null;
        if(StringUtils.isNotBlank(deviceType)) {
            log.debug("Material has no ID, search by material number: {} and Device type: {}", material.getMaterialNumber(), material.getDeviceType());
            optionalByMaterialNumber = materialService.findByMaterialNumberAndDeviceType(material.getMaterialNumber(), deviceType);
        } else {
            log.debug("Material has no ID, search by material number: {}", material.getMaterialNumber());
            String zone = materialPrice != null ? materialPrice.getZone() : null;
            optionalByMaterialNumber = materialService.findBy(salesOrg, salesOffice, material.getMaterialNumber(), deviceType, zone);
        }

        if(optionalByMaterialNumber.isPresent()) {
            return optionalByMaterialNumber.get();
        }

        log.debug("Material does not exist, returning material from request.");
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
        to.setDeviceType(from.getDeviceType());
        to.setPricingUnit(from.getPricingUnit());
        to.setMaterialNumber(from.getMaterialNumber());
        to.setQuantumUnit(from.getQuantumUnit());
    }
}
