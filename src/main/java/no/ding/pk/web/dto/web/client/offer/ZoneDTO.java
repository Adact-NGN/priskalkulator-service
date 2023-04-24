package no.ding.pk.web.dto.web.client.offer;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("number")
    String zoneId;
    String postalCode;
    String postalName;
    Boolean isStandardZone;
    @JsonAlias("materialList")
    List<PriceRowDTO> priceRows;
}
