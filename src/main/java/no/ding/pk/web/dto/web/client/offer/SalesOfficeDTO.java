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
public class SalesOfficeDTO {
    private Long id;
    private List<PriceRowDTO> materialList;
    private List<PriceRowDTO> rentalList;
    private List<PriceRowDTO> transportServiceList;
    @JsonAlias("zoneList")
    private List<ZoneDTO> zoneList;
    private String salesOffice;
    @JsonAlias({"name"})
    private String name;
    private String salesOrg;
    private String postalCode;
    private String city;
}
