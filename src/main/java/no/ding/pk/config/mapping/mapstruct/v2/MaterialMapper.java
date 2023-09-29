package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.offer.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MaterialMapper {
    @Mapping(target = "id", ignore = true)
    Material materialToMaterial(Material material);
}
