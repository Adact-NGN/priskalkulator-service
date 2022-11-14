package no.ding.pk.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;

@Service
public interface DiscountService {
    List<DiscountLevel> findDiscountBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg, String materialNumber, String salesOffice, int level);

    Discount save(Discount discount);

    Discount update(Long id, Discount discount);

    List<Discount> saveAll(List<Discount> discounts);

    DiscountLevel updateDiscountLevel(Long id, DiscountLevel discountLevel);

    List<Discount> findAll();

    List<Discount> findAllBySalesOrg(String salesOrg);

    List<Discount> findAllBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber);

    List<DiscountLevel> findDiscountBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg, String materialNumber,
            int level);
}
