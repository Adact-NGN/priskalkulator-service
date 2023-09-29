package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.User;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
    User userDtoToUser(UserDTO userDTO);
}
