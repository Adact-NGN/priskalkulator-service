package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesOfficeDTO {
    private Long id;
    private List<MaterialDTO> rentalList;
    private String salesOffice;
    private String salesOfficeName;
    private String salesOrg;
    private List<MaterialDTO> transportServiceList;
    private List<ZoneDTO> zones;
}
