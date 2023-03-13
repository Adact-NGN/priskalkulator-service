package no.ding.pk.service;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.repository.DiscountLevelRepository;
import no.ding.pk.repository.DiscountRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static no.ding.pk.repository.specifications.DiscountSpecifications.matchMaterialNumberInList;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withMaterialNumber;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withSalesOrg;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withZone;

@Transactional
@Service
public class DiscountServiceImpl implements DiscountService {

    private final static Logger log = LoggerFactory.getLogger(DiscountServiceImpl.class);

    private final DiscountRepository repository;
    private final DiscountLevelRepository discountLevelRepository;
    
    @Autowired
    public DiscountServiceImpl(DiscountRepository repository, DiscountLevelRepository discountLevelRepository) {
        this.repository = repository;
        this.discountLevelRepository = discountLevelRepository;
    }

    @Override
    public Discount save(Discount discount) {
        log.debug("Saving discount object ...");
        addDiscountLevelsToDiscountList(Collections.singletonList(discount));

        return repository.save(discount);
    }
    
    @Override
    public Discount update(Long id, Discount discount) {
        log.debug("Updating discount object, id:{}", id);
        Optional<Discount> opt = repository.findById(id);
        if(opt.isPresent()) {
            updateDiscountLevels(discount);
            return repository.save(discount);
        }
        return null;
    }

    private void updateDiscountLevels(Discount discount) {
        discount.getDiscountLevels().forEach(dl -> {
            if(dl.getParent() == null) {
                discount.addDiscountLevel(dl);
            }
        });
    }

    @Override
    public List<Discount> saveAll(List<Discount> discounts) {
        log.debug("Saving {} amount of Discount objects.", discounts.size());
        addDiscountLevelsToDiscountList(discounts);
        
        return repository.saveAll(discounts);
    }

    private void addDiscountLevelsToDiscountList(List<Discount> discounts) {
        for (Discount discount : discounts) {
            List<DiscountLevel> dlsToAdd = new ArrayList<>(discount.getDiscountLevels());

            for (DiscountLevel dl : dlsToAdd) {
                discount.addDiscountLevel(dl);
            }
        }
    }

    @Override
    public DiscountLevel updateDiscountLevel(Long id, DiscountLevel discountLevel) {
        Optional<DiscountLevel> opt = discountLevelRepository.findById(id);

        if(opt.isPresent()) {
            return discountLevelRepository.save(discountLevel);
        }
        return null;
    }

    @Override
    public List<Discount> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Discount> findAllBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber, String zones) {
        List<String> materialNumbers = Arrays.asList(materialNumber.split(","));

        if(StringUtils.isNotBlank(zones)) {
            log.debug("Zone is defined. Trying to get all materials in list with defined zone.");
            List <String> zoneList = Arrays.asList(zones.split(","));
            return repository.findAllBySalesOrgAndZoneInAndMaterialNumberIn(salesOrg, zoneList, materialNumbers);
        }

        log.debug("Zone is not defined. Trying to get all materials in list with no defined zone.");
        return repository.findAll(Specification.where(withSalesOrg(salesOrg).and(withZone(zones)).and(matchMaterialNumberInList(materialNumbers))));
    }

    @Override
    public List<Discount> findAllBySalesOrgAndZoneAndMaterialNumber(String salesOrg, String zone, String materialNumber) {
        return repository.findAll(Specification.where(withZone(zone)).and(withSalesOrg(salesOrg)).and(withMaterialNumber(materialNumber)));
    }

    @Override
    public List<DiscountLevel> findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg,
            String materialNumber, Integer level) {
        return discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumberAndLevel(salesOrg, materialNumber, level);
    }

    @Override
    public List<DiscountLevel> findAllDiscountLevelsForDiscountBySalesOrgAndMaterialNumber(String salesOrg,
                                                                                           String materialNumbers, String zone) {
        List<String> materialNumberList = Arrays.asList(materialNumbers.split(","));
        if(!StringUtils.isBlank(zone) && StringUtils.isNumeric(zone)) {
            return discountLevelRepository.findAllByParentSalesOrgAndParentZoneAndParentMaterialNumberInList(salesOrg, zone, materialNumberList);
        }
        return discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumberInList(salesOrg, materialNumberList);
    }
    
}
