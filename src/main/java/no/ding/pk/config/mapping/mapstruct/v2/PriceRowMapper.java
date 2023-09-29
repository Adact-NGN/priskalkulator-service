package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.web.dto.web.client.offer.PriceRowDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PriceRowMapper {

    @Mapping(source = "material", target = "material.materialNumber")
    @Mapping(source = "designation", target = "material.designation")
    @Mapping(source = "materialDesignation", target = "material.materialTypeDescription")
    @Mapping(source = "productGroupDesignation", target = "material.materialGroupDesignation")
    @Mapping(source = "deviceType", target = "material.deviceType")
    @Mapping(source = "pricingUnit", target = "material.pricingUnit")
    @Mapping(source = "quantumUnit", target = "material.quantumUnit")
    PriceRow priceRowDtoToPriceRow(PriceRowDTO priceRowDTO);

    @Mapping(target = "material", source = "material.materialNumber")
    @Mapping(target = "designation", source = "material.designation")
    @Mapping(target = "materialDesignation", source = "material.materialTypeDescription")
    @Mapping(target = "productGroupDesignation", source = "material.materialGroupDesignation")
    @Mapping(target = "deviceType", source = "material.deviceType")
    @Mapping(target = "pricingUnit", source = "material.pricingUnit")
    @Mapping(target = "quantumUnit", source = "material.quantumUnit")
    PriceRowDTO priceRowToPriceRowDto(PriceRow priceRow);
}
