package no.ding.pk.config.mapping.v2;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.web.dto.azure.ad.AdUserDTO;
import no.ding.pk.web.dto.web.client.offer.PriceRowDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperV2Config {

    @Bean(name = "modelMapperV2")
    public ModelMapper modelMapperV2(MaterialService materialRepository) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(AdUserDTO.class, User.class)
                .addMapping(AdUserDTO::getAdId, User::setAdId)
                .addMapping(AdUserDTO::getSureName, User::setSureName);

        priceRowDtoToPriceRowTypeMapping(materialRepository, modelMapper);

        priceRowToPriceRowDtoTypeMapping(modelMapper);

        return modelMapper;
    }

    private static void priceRowDtoToPriceRowTypeMapping(MaterialService materialRepository, ModelMapper modelMapper) {
        Converter<String, Material> stringToMaterial = c -> {
            if(c.getSource() != null) {
                Material material = materialRepository.findByMaterialNumber(c.getSource());

                if(material != null) {
                    return material;
                }

                return Material.builder().materialNumber(c.getSource()).build();
            }

            return null;
        };

        TypeMap<PriceRowDTO, PriceRow> priceRowDtoPropertyMap = modelMapper.createTypeMap(PriceRowDTO.class, PriceRow.class);
        priceRowDtoPropertyMap.addMappings(mapper -> mapper.using(stringToMaterial).map(PriceRowDTO::getMaterial, PriceRow::setMaterial));
    }

    private static void priceRowToPriceRowDtoTypeMapping(ModelMapper modelMapper) {
        TypeMap<PriceRow, PriceRowDTO> priceRowTypeMap = modelMapper.createTypeMap(PriceRow.class, PriceRowDTO.class);

        mapHierarchyValuesForPriceRowDto(priceRowTypeMap);

        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialNumber(), PriceRowDTO::setMaterial));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getDesignation(), PriceRowDTO::setMaterialDesignation));
    }

    private static void mapHierarchyValuesForPriceRowDto(TypeMap<PriceRow, PriceRowDTO> priceRowTypeMap) {
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getCategoryId(), PriceRowDTO::setCategoryId));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getCategoryDescription(), PriceRowDTO::setCategoryDescription));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getSubCategoryId(), PriceRowDTO::setSubCategoryId));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getSubCategoryDescription(), PriceRowDTO::setSubCategoryDescription));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getClassId(), PriceRowDTO::setClassId));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getClassDescription(), PriceRowDTO::setClassDescription));
    }
}
