package no.ding.pk.web.dto.v2.web.client.offer;

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
public class
ZoneDTO {
    Long id;
    String salesOffice;
    String number;
    String postalCode;
    String postalName;
    Boolean isStandardZone;
    List<PriceRowDTO> materialList;
}
