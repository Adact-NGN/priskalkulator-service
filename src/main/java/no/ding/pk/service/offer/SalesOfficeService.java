package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.offer.SalesOffice;

import java.util.List;
import java.util.Map;

public interface SalesOfficeService {

    List<SalesOffice> saveAll(List<SalesOffice> salesOfficeList, String customerNumber, Map<String, Map<String, Map<String, Discount>>> salesOrgSalesOfficeMaterialDiscountMap);

}
