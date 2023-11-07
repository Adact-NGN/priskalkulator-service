package no.ding.pk.web.dto.web.client.offer.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PriceOfferTemplateDTO {
    private Long id;
    private String name;
    private Boolean isShareable;
    private String author;
    private List<String> sharedWith;
    private List<TemplateMaterialDTO> materials;
    private List<TemplateMaterialDTO> zoneBasedMaterials;
}
