package no.ding.pk.service.offer;

import com.fasterxml.jackson.databind.ObjectWriter;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.sap.StandardPriceService;
import no.ding.pk.web.dto.sap.MaterialDTO;
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

    private ObjectWriter objectWriter;
    
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
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice) {
        return saveAll(priceRowList, salesOrg, salesOffice, null);
    }
    
    @Override
    public List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice, String zone) {
        List<PriceRow> returnList = new ArrayList<>();
        for(int i = 0; i < priceRowList.size(); i++) {
            PriceRow materialPriceRow = priceRowList.get(i);
            PriceRow entity = save(materialPriceRow, salesOrg, salesOffice, zone);

            returnList.add(entity);
        }
        
        log.debug("Collected {} amount of PriceRows", returnList.size());
        return returnList;
    }

    private PriceRow save(PriceRow materialPriceRow, String salesOrg, String salesOffice, String zone) {
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
        entity.setDiscountLevel(materialPriceRow.getDiscountLevel());
        entity.setDiscountLevelPrice(materialPriceRow.getDiscountLevelPrice());
        entity.setStandardPrice(materialPriceRow.getStandardPrice());
        entity.setAmount(materialPriceRow.getAmount());
        entity.setPriceIncMva(materialPriceRow.getPriceIncMva());

        if(materialPriceRow.getMaterial() != null) {
            Material material = materialPriceRow.getMaterial();
            log.debug("PriceRow->Material: {}", material);

            EntityManager em = emFactory.createEntityManager();
            log.debug("Is material attached: {}", em.contains(material));

            if(material.getId() == null) {

                em.getTransaction().begin();
                List materials = em.createNamedQuery("findMaterialByMaterialNumber").setParameter("materialNumber", material.getMaterialNumber()).getResultList();
                em.getTransaction().commit();
                em.close();
                //                    Material persistedMaterial = materialService.findByMaterialNumber(material.getMaterialNumber());

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
                    }

                    persistedMaterial = materialService.save(persistedMaterial);

                    entity.setMaterial(persistedMaterial);
                } else {
                    log.debug("New Material to create with material number: {}", material.getMaterialNumber());
                    MaterialDTO sapMaterial = sapMaterialService.getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(material.getMaterialNumber(), salesOrg, salesOffice, zone);

                    if(sapMaterial != null) {
                        Material fromSap = modelMapper.map(sapMaterial, Material.class);

                        material = materialService.save(fromSap);
                    } else {
                        log.debug("Could not find material {} for salesorg {}", material.getMaterialNumber(), salesOrg);
                    }

                    entity.setMaterial(material);
                }

            } else {
                material = materialService.save(material);
                entity.setMaterial(material);
            }

        }

        if(materialPriceRow.hasCombinedMaterials()) {
            List<PriceRow> combinedMaterialPriceRows = new ArrayList<>();

            for(int j = 0; j < materialPriceRow.getCombinedMaterials().size(); j++) {
                PriceRow combinedMaterial = this.save(materialPriceRow.getCombinedMaterials().get(j), salesOrg, salesOffice, zone);
                combinedMaterialPriceRows.add(combinedMaterial);
            }

            entity.setCombinedMaterials(combinedMaterialPriceRows);
        }

        return repository.save(entity);
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
    }
    
    private void updateMaterialPrice(MaterialPrice to, MaterialPrice from) {
        log.debug("To: {}, from: {}", to, from);
        to.setStandardPrice(from.getStandardPrice());
        to.setValidFrom(from.getValidFrom());
        to.setValidTo(from.getValidTo());
    }
    
}
