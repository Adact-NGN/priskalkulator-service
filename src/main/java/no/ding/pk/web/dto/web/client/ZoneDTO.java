package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.offer.PriceRow;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {
    Long id;
    String zoneId;
    String postalCode;
    String postalName;
    Boolean isStandardZone;
    List<PriceRow> priceRows;
}
