package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {
    Long id;
    Integer zoneId;
    String postalCode;
    String postalName;
    Boolean isStandardZone;
    List<PriceRowDTO> priceRows;
}
