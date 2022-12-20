package no.ding.pk.service;

import java.util.List;

import org.springframework.stereotype.Service;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;

@Service
public interface DiscountService {

    Discount save(Discount discount);

    Discount update(Long id, Discount discount);

    List<Discount> saveAll(List<Discount> discounts);

    DiscountLevel updateDiscountLevel(Long id, DiscountLevel discountLevel);

    List<Discount> findAll();

    List<Discount> findAllBySalesOrgAndZoneAndMaterialNumber(String salesOrg, String zone, String materialNumber);

    List<Discount> findAllBySalesOrgAndMaterialNumber(String salesOrg, String materialNumber);

    List<DiscountLevel> findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg, String materialNumber,
            Integer level);

    List<DiscountLevel> findAllDiscountLevelsForDiscountBySalesOrgAndMaterialNumber(String salesOrg, String materialNumbers, String zone);
}
