package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;

import java.util.List;
import java.util.Map;

public interface PriceRowService {

    List<PriceRow> saveAll(List<PriceRow> materialList, String salesOrg, String salesOffice,
                           Map<String, MaterialPrice> materialStdPriceMap,
                           Map<String, Map<String, Map<String, Discount>>> discountMap);
    List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice, String zone,
                           Map<String, MaterialPrice> materialStdPriceMap,
                           Map<String, Map<String, Map<String, Discount>>> discountMap);

}
