package no.ding.pk.service.offer;

import java.util.List;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;

public interface PriceRowService {

    List<PriceRow> saveAll(List<PriceRow> materialList, String salesOrg, String salesOffice, List<MaterialPrice> materialStdPrices);
    List<PriceRow> saveAll(List<PriceRow> priceRowList, String salesOrg, String salesOffice, String zone, List<MaterialPrice> materialStdPrices);

}
