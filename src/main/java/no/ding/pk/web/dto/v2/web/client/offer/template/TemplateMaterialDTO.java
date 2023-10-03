package no.ding.pk.web.dto.v2.web.client.offer.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TemplateMaterialDTO {
    private Long id;
    private String material;
    private String deviceType;
}
