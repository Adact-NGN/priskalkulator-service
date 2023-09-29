package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SapMaterialMapper {
    @Mapping(target = "materialNumber", source = "material")
    @Mapping(target = "designation", source = "materialDescription")
    @Mapping(target = "materialGroupDesignation", source = "materialGroupDescription")
    @Mapping(target = "materialTypeDescription", source = "materialTypeDescription")
    @Mapping(target = "quantumUnit", source = "weightUnit")
    @Mapping(target = "scaleQuantum", source = "netWeight")
    Material sapMaterialDtoToMaterial(MaterialDTO sapMaterialDto);


    MaterialDTO materialToSapMaterialDto(Material material);
}
