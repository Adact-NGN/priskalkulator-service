package no.ding.pk.service;

import java.util.List;

import no.ding.pk.web.dto.MaterialDTO;

public interface StandardPriceService {
    List<MaterialDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg);
}
