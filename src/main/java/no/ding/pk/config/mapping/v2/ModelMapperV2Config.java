package no.ding.pk.config.mapping.v2;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.web.dto.azure.ad.AdUserDTO;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.dto.web.client.UserDTO;
import no.ding.pk.web.dto.web.client.offer.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class ModelMapperV2Config {

    private static final Logger log = LoggerFactory.getLogger(ModelMapperV2Config.class);

    @Bean(name = "modelMapperV2")
    public ModelMapper modelMapperV2(MaterialService materialRepository, SalesRoleRepository salesRoleRepository) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        priceOfferDtoToPriceOfferMapping(modelMapper);

        priceOfferToPriceOfferDtoMapping(modelMapper);

        modelMapper.typeMap(AdUserDTO.class, User.class)
                .addMapping(AdUserDTO::getAdId, User::setAdId)
                .addMapping(AdUserDTO::getSureName, User::setSureName);

        modelMapper.typeMap(MaterialDTO.class, Material.class)
                .addMapping(MaterialDTO::getMaterial, Material::setMaterialNumber)
                .addMapping(MaterialDTO::getMaterialDescription, Material::setDesignation)
                .addMapping(MaterialDTO::getMaterialGroupDescription, Material::setMaterialGroupDesignation)
                .addMapping(MaterialDTO::getMaterialTypeDescription, Material::setMaterialTypeDescription)
                .addMapping(MaterialDTO::getWeightUnit, Material::setQuantumUnit)
                .addMapping(MaterialDTO::getNetWeight, Material::setScaleQuantum)
                .addMapping(MaterialDTO::getCategoryId, Material::setCategoryId)
                .addMapping(MaterialDTO::getCategoryDescription, Material::setCategoryDescription)
                .addMapping(MaterialDTO::getSubCategoryId, Material::setSubCategoryId)
                .addMapping(MaterialDTO::getSubCategoryDescription, Material::setSubCategoryDescription)
                .addMapping(MaterialDTO::getClassId, Material::setClassId)
                .addMapping(MaterialDTO::getClassDescription, Material::setClassDescription)
        ;

        modelMapper.typeMap(ZoneDTO.class, Zone.class)
                .addMapping(ZoneDTO::getNumber, Zone::setZoneId)
                .addMapping(ZoneDTO::getMaterialList, Zone::setPriceRows);

        modelMapper.typeMap(Zone.class, ZoneDTO.class)
                .addMapping(Zone::getZoneId, ZoneDTO::setNumber)
                .addMapping(Zone::getPriceRows, ZoneDTO::setMaterialList);

        modelMapper.typeMap(SalesOffice.class, SalesOfficeDTO.class)
                        .addMapping(SalesOffice::getSalesOfficeName, SalesOfficeDTO::setName);

        modelMapper.typeMap(SalesOfficeDTO.class, SalesOffice.class)
                        .addMapping(SalesOfficeDTO::getName, SalesOffice::setSalesOfficeName);

        priceOfferToPriceOfferListDto(modelMapper);

        priceRowDtoToPriceRowTypeMapping(materialRepository, modelMapper);

        priceRowToPriceRowDtoTypeMapping(modelMapper);

        userDtoToUserTypeMapping(modelMapper, salesRoleRepository);

        modelMapper.typeMap(MaterialStdPriceDTO.class, MaterialPrice.class)
                .addMapping(MaterialStdPriceDTO::getMaterial, MaterialPrice::setMaterialNumber);

        return modelMapper;
    }

    private static void priceOfferToPriceOfferListDto(ModelMapper modelMapper) {
        modelMapper.createTypeMap(User.class, SimpleSalesEmployeeDTO.class).addMappings(mapping -> mapping.map(User::getFullName, SimpleSalesEmployeeDTO::setFullName));

        modelMapper.typeMap(PriceOffer.class, PriceOfferListDTO.class)
                        .addMapping(PriceOffer::getId, PriceOfferListDTO::setId)
                .addMapping(PriceOffer::getPriceOfferStatus, PriceOfferListDTO::setPriceOfferStatus)
                .addMapping(PriceOffer::getCreatedDate, PriceOfferListDTO::setDateCreated)
                .addMapping(PriceOffer::getLastModifiedDate, PriceOfferListDTO::setDateUpdated)
                .addMapping(PriceOffer::getCustomerName, PriceOfferListDTO::setCustomerName)
                .addMapping(PriceOffer::getCustomerNumber, PriceOfferListDTO::setCustomerNumber);
    }

    private static void priceOfferToPriceOfferDtoMapping(ModelMapper modelMapper) {
        Converter<List<ContactPerson>, ContactPersonDTO> entityToDto = c -> {
            if(c.getSource() != null) {
                if(!c.getSource().isEmpty()) {
                    return modelMapper.map(c.getSource().get(0), ContactPersonDTO.class);
                }
            }

            return null;
        };

        modelMapper.typeMap(PriceOffer.class, PriceOfferDTO.class)
                .addMapping(PriceOffer::getCreatedDate, PriceOfferDTO::setDateCreated)
                .addMapping(PriceOffer::getLastModifiedDate, PriceOfferDTO::setDateUpdated)
                .addMapping(PriceOffer::getDismissalReason, PriceOfferDTO::setDismissalReason)
                .addMappings(mapping -> mapping.using(entityToDto).map(PriceOffer::getContactPersonList, PriceOfferDTO::setContactPerson));
    }

    private static void priceOfferDtoToPriceOfferMapping(ModelMapper modelMapper) {
        Converter<ContactPersonDTO, List<ContactPerson>> dtoToList = c -> {
            if(c.getSource() != null) {
                ContactPerson contactPerson = modelMapper.map(c.getSource(), ContactPerson.class);

                return List.of(contactPerson);
            }

            return null;
        };

        modelMapper.typeMap(PriceOfferDTO.class, PriceOffer.class)
                .addMapping(PriceOfferDTO::getDismissalReason, PriceOffer::setDismissalReason)
                .addMappings(mapping -> mapping.using(dtoToList).map(PriceOfferDTO::getContactPerson, PriceOffer::setContactPersonList));
    }

    private static void priceRowDtoToPriceRowTypeMapping(MaterialService materialRepository, ModelMapper modelMapper) {
        // https://amydegregorio.com/2018/01/17/using-custom-modelmapper-converters-and-mappings/
        Converter<String, Material> stringToMaterial = c -> {
            Material material = null;
            if(c.getSource() != null) {
                String[] materialDeviceTypeId = c.getSource().split("_");

                if(materialDeviceTypeId.length > 1) {
                    material = materialRepository.findByMaterialNumberAndDeviceType(materialDeviceTypeId[0], materialDeviceTypeId[1]);
                } else {
                    material = materialRepository.findByMaterialNumber(c.getSource());
                }

                if(material != null) {
                    return material;
                }

                log.debug("No material number was found. Material object must be created.");

                if(materialDeviceTypeId.length > 1) {
                    material = Material.builder().materialNumber(materialDeviceTypeId[0]).deviceType(materialDeviceTypeId[1]).build();
                } else {
                    material = Material.builder().materialNumber(c.getSource()).build();
                }
            }

            return material;
        };

        TypeMap<PriceRowDTO, PriceRow> priceRowDtoPropertyMap = modelMapper.createTypeMap(PriceRowDTO.class, PriceRow.class);
        priceRowDtoPropertyMap.addMappings(mapper -> mapper.using(stringToMaterial).map(PriceRowDTO::getMaterialId, PriceRow::setMaterial));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getCategoryId, (destination, value) -> destination.getMaterial().setCategoryId((String) value)));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getCategoryDescription, (destination, value) -> destination.getMaterial().setCategoryDescription((String) value)));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getDeviceType, (destination, value) -> destination.getMaterial().setDeviceType((String) value)));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getSubCategoryId, (destination, value) -> destination.getMaterial().setSubCategoryId((String) value)));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getSubCategoryDescription, (destination, value) -> destination.getMaterial().setSubCategoryDescription((String) value)));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getClassId, (destination, value) -> destination.getMaterial().setClassId((String) value)));
        priceRowDtoPropertyMap.addMappings(mapping -> mapping.map(PriceRowDTO::getClassDescription, (destination, value) -> destination.getMaterial().setClassDescription((String) value)));
    }

    private static void priceRowToPriceRowDtoTypeMapping(ModelMapper modelMapper) {
        TypeMap<PriceRow, PriceRowDTO> priceRowTypeMap = modelMapper.createTypeMap(PriceRow.class, PriceRowDTO.class);

        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialNumber(), PriceRowDTO::setMaterial));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getDesignation(), PriceRowDTO::setDesignation));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialTypeDescription(), PriceRowDTO::setMaterialDesignation));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getMaterialGroupDesignation(), PriceRowDTO::setProductGroupDesignation));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getDeviceType(), PriceRowDTO::setDeviceType));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getPricingUnit(), PriceRowDTO::setPricingUnit));
        priceRowTypeMap.addMappings(mapper -> mapper.map(src -> src.getMaterial().getQuantumUnit(), PriceRowDTO::setQuantumUnit));
        priceRowTypeMap.addMapping(PriceRow::getCategoryId, PriceRowDTO::setCategoryId);
        priceRowTypeMap.addMapping(PriceRow::getCategoryDescription, PriceRowDTO::setCategoryDescription);
        priceRowTypeMap.addMapping(PriceRow::getSubCategoryId, PriceRowDTO::setSubCategoryId);
        priceRowTypeMap.addMapping(PriceRow::getSubCategoryDescription, PriceRowDTO::setSubCategoryDescription);
        priceRowTypeMap.addMapping(PriceRow::getClassId, PriceRowDTO::setClassId);
        priceRowTypeMap.addMapping(PriceRow::getClassDescription, PriceRowDTO::setClassDescription);
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
