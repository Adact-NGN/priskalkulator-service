package no.ding.pk.config.mapping.v2;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.web.dto.azure.ad.AdUserDTO;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.web.client.UserDTO;
import no.ding.pk.web.dto.web.client.offer.PriceRowDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ModelMapperV2Config {

    private static final Logger log = LoggerFactory.getLogger(ModelMapperV2Config.class);

    @Bean(name = "modelMapperV2")
    public ModelMapper modelMapperV2(MaterialService materialRepository, SalesRoleRepository salesRoleRepository) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

//        priceOfferDtoToPriceOfferMapping(modelMapper);

//        termsToPriceOfferTermsMapping(modelMapper);

        modelMapper.typeMap(AdUserDTO.class, User.class)
                .addMapping(AdUserDTO::getAdId, User::setAdId)
                .addMapping(AdUserDTO::getSureName, User::setSureName);

        modelMapper.typeMap(MaterialDTO.class, Material.class)
                .addMapping(MaterialDTO::getMaterial, Material::setMaterialNumber)
                .addMapping(MaterialDTO::getMaterialDescription, Material::setDesignation)
                .addMapping(MaterialDTO::getMaterialGroupDescription, Material::setMaterialGroupDesignation)
                .addMapping(MaterialDTO::getMaterialTypeDescription, Material::setMaterialTypeDescription)
                .addMapping(MaterialDTO::getWeightUnit, Material::setQuantumUnit)
                .addMapping(MaterialDTO::getNetWeight, Material::setScaleQuantum);

        priceRowDtoToPriceRowTypeMapping(materialRepository, modelMapper);

        priceRowToPriceRowDtoTypeMapping(modelMapper);

        userDtoToUserTypeMapping(modelMapper, salesRoleRepository);

        return modelMapper;
    }

    private static void priceRowDtoToPriceRowTypeMapping(MaterialService materialRepository, ModelMapper modelMapper) {
        Converter<String, Material> stringToMaterial = c -> {
            if(c.getSource() != null) {
                log.debug("Converting {} to material object", c.getSource());
                Material material = materialRepository.findByMaterialNumber(c.getSource());

                if(material != null) {
                    return material;
                }

                return Material.builder().materialNumber(c.getSource()).build();
            }

            log.debug("No material number was found. Material object could not be created.");
            return null;
        };

        TypeMap<PriceRowDTO, PriceRow> priceRowDtoPropertyMap = modelMapper.createTypeMap(PriceRowDTO.class, PriceRow.class);
        priceRowDtoPropertyMap.addMappings(mapper -> mapper.using(stringToMaterial).map(PriceRowDTO::getMaterial, PriceRow::setMaterial));
    }

    private static void priceRowToPriceRowDtoTypeMapping(ModelMapper modelMapper) {
        TypeMap<PriceRow, PriceRowDTO> priceRowTypeMap = modelMapper.createTypeMap(PriceRow.class, PriceRowDTO.class);

        mapHierarchyValuesForPriceRowDto(priceRowTypeMap);

        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialNumber(), PriceRowDTO::setMaterial));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getDesignation(), PriceRowDTO::setDesignation));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialTypeDescription(), PriceRowDTO::setMaterialDesignation));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialGroupDesignation(), PriceRowDTO::setProductGroupDesignation));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getDeviceType(), PriceRowDTO::setDeviceType));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getPricingUnit(), PriceRowDTO::setPricingUnit));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getQuantumUnit(), PriceRowDTO::setQuantumUnit));
    }

    private static void mapHierarchyValuesForPriceRowDto(TypeMap<PriceRow, PriceRowDTO> priceRowTypeMap) {
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getCategoryId(), PriceRowDTO::setCategoryId));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getCategoryDescription(), PriceRowDTO::setCategoryDescription));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getSubCategoryId(), PriceRowDTO::setSubCategoryId));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getSubCategoryDescription(), PriceRowDTO::setSubCategoryDescription));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getClassId(), PriceRowDTO::setClassId));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getClassDescription(), PriceRowDTO::setClassDescription));
    }

    private static void userDtoToUserTypeMapping(ModelMapper modelMapper, SalesRoleRepository salesRoleRepository) {
        TypeMap<UserDTO, User> userDtoToUserTypeMap = modelMapper.createTypeMap(UserDTO.class, User.class);
        Converter<Long, SalesRole> salesRoleIdToEntity = c -> {
            if(c.getSource() != null) {
                Optional<SalesRole> byId = salesRoleRepository.findById(c.getSource());

                if(byId.isPresent())
                    return byId.get();
            }

            return null;
        };

        userDtoToUserTypeMap.addMappings(mapper -> mapper.using(salesRoleIdToEntity).map(UserDTO::getSalesRoleId, User::setSalesRole));
    }
}
