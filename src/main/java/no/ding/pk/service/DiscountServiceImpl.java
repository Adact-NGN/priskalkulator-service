package no.ding.pk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        discount.getDiscountLevelList().stream().forEach(dl -> discount.addDiscountLevel(dl));
        return repository.save(discount);
    }
    
    @Override
    public Discount update(Long id, Discount discount) {
        Optional<Discount> opt = repository.findById(id);
        if(opt.isPresent()) {
            return repository.save(discount);
        }
        return null;
    }

    @Override
    public List<Discount> saveAll(List<Discount> discounts) {
        discounts.stream().forEach(d -> {
            d.getDiscountLevelList().stream().forEach(dl -> d.addDiscountLevel(dl));
        });
        return repository.saveAll(discounts);
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
    public List<DiscountLevel> findDiscountBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg,
            String materialNumber, int level) {
        return discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumberAndLevel(salesOrg, materialNumber, level);
    }
    
}
