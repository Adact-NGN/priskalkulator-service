package no.ding.pk.service.offer;

import java.util.List;
import java.util.Map;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;

public interface PriceRowService {

    List<PriceRow> saveAll(List<PriceRow> materialList, String salesOrg, String salesOffice,
                           List<MaterialPrice> materialStdPrices,
                           Map<String, Map<String, Map<String, Discount>>> discountMap);
    List<PriceRow> saveAll(List<PriceRow> materialList, String salesOrg, String salesOffice, String zone,
                           List<MaterialPrice> materialStdPrices,
                           Map<String, Map<String, Map<String, Discount>>> discountMap);

}
