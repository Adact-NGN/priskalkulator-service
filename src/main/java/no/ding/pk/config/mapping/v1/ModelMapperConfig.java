package no.ding.pk.config.mapping.v1;

import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.web.dto.azure.ad.AdUserDTO;
import no.ding.pk.web.dto.v1.web.client.DiscountLevelDTO;
import no.ding.pk.web.dto.v1.web.client.MaterialDTO;
import no.ding.pk.web.dto.v1.web.client.PriceRowDTO;
import no.ding.pk.web.dto.web.client.SalesOfficePowerOfAttorneyDTO;
import no.ding.pk.web.dto.web.client.UserDTO;
import no.ding.pk.web.dto.web.client.offer.ZoneDTO;
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
public class ModelMapperConfig {

    private static final Logger log = LoggerFactory.getLogger(ModelMapperConfig.class);

    @Bean
    public ModelMapper modelMapper(SalesRoleRepository salesRoleRepository, UserRepository userRepository) {
        log.debug("Creating model mapper.");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(AdUserDTO.class, User.class)
                .addMapping(AdUserDTO::getAdId, User::setAdId)
                .addMapping(AdUserDTO::getSureName, User::setSureName);

        modelMapper.typeMap(MaterialDTO.class, Material.class)
                .addMapping(MaterialDTO::getMaterial, Material::setMaterialNumber);

        modelMapper.typeMap(ZoneDTO.class, Zone.class)
                .addMapping(ZoneDTO::getNumber, Zone::setZoneId)
                .addMapping(ZoneDTO::getMaterialList, Zone::setPriceRows);

        modelMapper.typeMap(Zone.class, ZoneDTO.class)
                .addMapping(Zone::getZoneId, ZoneDTO::setNumber)
                .addMapping(Zone::getPriceRows, ZoneDTO::setMaterialList);

        priceRowDtoToPriceRowTypeMapping(modelMapper);

        userToUserDtoTypeMapping(modelMapper);

        userDtoToUserTypeMapping(modelMapper, salesRoleRepository);

        powerOfAttorneyToSalesOfficePowerOfAttorneyDto(modelMapper);

        salesOfficePowerOfAttorneyDtoToPowerOfAttorney(modelMapper, userRepository);

        return modelMapper;
    }

    private void salesOfficePowerOfAttorneyDtoToPowerOfAttorney(ModelMapper modelMapper, UserRepository userRepository) {

        Converter<String, User> emailStringToUser = c -> {
            if(c.getSource() != null) {
                return userRepository.findByEmailIgnoreCase(c.getSource());
            }

            return null;
        };

        modelMapper.typeMap(SalesOfficePowerOfAttorneyDTO.class, PowerOfAttorney.class)
                .addMappings(mapper -> mapper.using(emailStringToUser).map(SalesOfficePowerOfAttorneyDTO::getMailOrdinaryWasteLvlOne, PowerOfAttorney::setOrdinaryWasteLvlOneHolder))
                .addMappings(mapper -> mapper.using(emailStringToUser).map(SalesOfficePowerOfAttorneyDTO::getMailOrdinaryWasteLvlTwo, PowerOfAttorney::setOrdinaryWasteLvlTwoHolder))
                .addMappings(mapper -> mapper.using(emailStringToUser).map(SalesOfficePowerOfAttorneyDTO::getDangerousWaste, PowerOfAttorney::setDangerousWasteHolder));
    }

    private static void powerOfAttorneyToSalesOfficePowerOfAttorneyDto(ModelMapper modelMapper) {
        Converter<User, String> userToEmailString = c -> {
            if(c.getSource() != null) {
                return c.getSource().getEmail();
            }

            return "";
        };

        modelMapper.createTypeMap(PowerOfAttorney.class, SalesOfficePowerOfAttorneyDTO.class)
                .addMappings(mapper -> mapper.using(userToEmailString).map(PowerOfAttorney::getOrdinaryWasteLvlOneHolder, SalesOfficePowerOfAttorneyDTO::setMailOrdinaryWasteLvlOne))
                .addMappings(mapper -> mapper.using(userToEmailString).map(PowerOfAttorney::getOrdinaryWasteLvlTwoHolder, SalesOfficePowerOfAttorneyDTO::setMailOrdinaryWasteLvlTwo))
                .addMappings(mapper -> mapper.using(userToEmailString).map(PowerOfAttorney::getDangerousWasteHolder, SalesOfficePowerOfAttorneyDTO::setDangerousWaste));
    }

    private static void userToUserDtoTypeMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getSalesRole().getId(), UserDTO::setSalesRoleId));
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

    private static void priceRowDtoToPriceRowTypeMapping(ModelMapper modelMapper) {
        TypeMap<PriceRowDTO, PriceRow> priceRowPropertyMap = modelMapper.createTypeMap(PriceRowDTO.class, PriceRow.class);
        Converter<DiscountLevelDTO, Integer> materialDiscountDtoToInteger = c -> {
            if(c.getSource() != null && c.getSource().getLevel() != null) {
                return Integer.parseInt(c.getSource().getLevel());
            }

            return null;
        };
        Converter<DiscountLevelDTO, Double> discountLevelDiscount = c -> {
            if(c.getSource() != null && c.getSource().getDiscount() != null) {
                return c.getSource().getDiscount();
            }
            return null;
        };

        priceRowPropertyMap
                .addMappings(mapper -> mapper.using(materialDiscountDtoToInteger).map(PriceRowDTO::getDiscountLevel, PriceRow::setDiscountLevel))
                .addMappings(mapper -> mapper.using(discountLevelDiscount).map(PriceRowDTO::getDiscountLevel, PriceRow::setDiscountLevelPrice));
    }
}
