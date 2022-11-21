package no.ding.pk.service;

import static no.ding.pk.repository.specifications.DiscountSpecifications.withZone;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withSalesOrg;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withMaterialNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.repository.DiscountLevelRepository;
import no.ding.pk.repository.DiscountRepository;

@Transactional
@Service
public class DiscountServiceImpl implements DiscountService {

    private final static Logger log = LoggerFactory.getLogger(DiscountServiceImpl.class);

    private DiscountRepository repository;
    private DiscountLevelRepository discountLevelRepository;
    
    @Autowired
    public DiscountServiceImpl(DiscountRepository repository, DiscountLevelRepository discountLevelRepository) {
        this.repository = repository;
        this.discountLevelRepository = discountLevelRepository;
    }
    
    @Override
    public List<DiscountLevel> findDiscountBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg,
    String materialNumber, String salesOffice, int level) {
        return discountLevelRepository.findBySalesOrgAndMaterialNumberAndLevel(salesOrg, materialNumber, level);
    }

    @Override
    public Discount save(Discount discount) {
        addDiscountLevelsToDiscounList(Arrays.asList(discount));

        return repository.save(discount);
    }
    
    @Override
    public Discount update(Long id, Discount discount) {
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
        addDiscountLevelsToDiscounList(discounts);
        
        return repository.saveAll(discounts);
    }

    private void addDiscountLevelsToDiscounList(List<Discount> discounts) {
        for(Iterator<Discount> iterator = discounts.iterator(); iterator.hasNext();) {
            Discount discount = iterator.next();
            List<DiscountLevel> dlsToAdd = new ArrayList<>();
            for(Iterator<DiscountLevel> dlIterator = discount.getDiscountLevels().iterator(); dlIterator.hasNext();) {
                DiscountLevel dl = dlIterator.next();

                dlsToAdd.add(dl);
            }

            for(DiscountLevel dl: dlsToAdd) {
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
    public List<Discount> findAllBySalesOrg(String salesOrg) {
        return repository.findAllBySalesOrg(salesOrg);
    }

    @Override
    public List<Discount> findAllBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber) {
        return repository.findAllBySalesOrgAndMaterialNumber(salesOrg, materialNumber);
    }

    @Override
    public List<Discount> findAllBySalesOrgAndZoneAndMaterialNumber(String salesOrg, String zone, String materialNumber) {
        return repository.findAll(Specification.where(withZone(zone)).and(withSalesOrg(salesOrg)).and(withMaterialNumber(materialNumber)));
    }

    @Override
    public List<DiscountLevel> findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg,
            String materialNumber, int level) {
        return discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumberAndLevel(salesOrg, materialNumber, level);
    }

    @Override
    public List<DiscountLevel> findAllDiscountLevelsForDiscountBySalesOrgAndMaterialNumber(String salesOrg,
            String materialNumber) {
        // TODO Auto-generated method stub
        return discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumber(salesOrg, materialNumber);
    }
    
}
