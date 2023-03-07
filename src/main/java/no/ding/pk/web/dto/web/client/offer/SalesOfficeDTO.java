package no.ding.pk.web.dto.web.client.offer;

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
public class SalesOfficeDTO {
    private Long id;
    private List<PriceRowDTO> materialList;
    private List<PriceRowDTO> rentalList;
    private List<PriceRowDTO> transportServiceList;
    private List<ZoneDTO> zones;
    private String salesOffice;
    private String salesOfficeName;
    private String salesOrg;
}
