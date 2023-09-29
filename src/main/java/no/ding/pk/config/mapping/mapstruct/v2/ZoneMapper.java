package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.offer.Zone;
import no.ding.pk.web.dto.web.client.offer.ZoneDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ZoneMapper {
    @Mapping(target = "number", source = "zoneId")
    @Mapping(target = "materialList", source = "priceRows")
    ZoneDTO zoneToZoneDto(Zone zone);

    @Mapping(target = "zoneId", source = "number")
    @Mapping(target = "priceRows", source = "materialList")
    Zone zoneDtoToZone(ZoneDTO zoneDTO);
}
