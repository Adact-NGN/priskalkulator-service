package no.ding.pk.web.dto.v2.web.client.offer.patch;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public class SalesOfficeUpdateDto {
    private JsonNullable<Long> id;

    private JsonNullable<List<PriceRowUpdateDto>> materialList;
    private JsonNullable<List<PriceRowUpdateDto>> rentalList;
    private JsonNullable<List<PriceRowUpdateDto>> transportServiceList;

    private JsonNullable<List<ZoneUpdateDto>> zoneList;

    private JsonNullable<String> salesOffice;
    private JsonNullable<String> name;
    private JsonNullable<String> salesOrg;
    private JsonNullable<String> postalCode;
    private JsonNullable<String> city;
}
