package no.ding.pk.service;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;

import java.util.List;

public interface DiscountService {

    Discount save(Discount discount);

    Discount update(Long id, Discount discount);

    List<Discount> saveAll(List<Discount> discounts);

    DiscountLevel updateDiscountLevel(Long id, DiscountLevel discountLevel);

    List<Discount> findAll();

    List<Discount> findAllBySalesOrgAndZoneAndMaterialNumber(String salesOrg, String zone, String materialNumber);

    List<Discount> findAllBySalesOrgAndSalesOfficeAndZoneAndMaterialNumber(String salesOrg, String salesOffice, String zone, String materialNumber);

    List<Discount> findAllBySalesOrgAndSalesOfficeAndMaterialNumber(String salesOrg, String salesOffice, String materialNumber, String zone);

    List<DiscountLevel> findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(String salesOrg, String salesOffice, String materialNumber,
            Integer level);

    List<DiscountLevel> findAllDiscountLevelsForDiscountBySalesOrgAndSalesOfficeAndMaterialNumber(String salesOrg,
                                                                                                  String salesOffice,
                                                                                                  String materialNumbers, String zone);

    List<Discount> findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(String salesOrg, String salesOffice, List<String> materials);
}
