package no.ding.pk.web.dto.v2.web.client.offer.patch;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public class ZoneUpdateDto {
    private JsonNullable<Long> id;
    private JsonNullable<String> salesOffice;
    private JsonNullable<String> number;
    private JsonNullable<String> postalCode;
    private JsonNullable<String> postalName;
    private JsonNullable<Boolean> isStandardZone;
    private JsonNullable<List<PriceRowUpdateDto>> materialList;
}
