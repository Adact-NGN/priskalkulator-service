package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.SalesOffice;

import java.util.List;

public interface SalesOfficeService {

    List<SalesOffice> saveAll(List<SalesOffice> salesOfficeList, String customerNumber);

}
