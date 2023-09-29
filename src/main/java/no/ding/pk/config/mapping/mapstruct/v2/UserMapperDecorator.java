package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public abstract class UserMapperDecorator implements UserMapper {

    private final UserMapper delegate;
    private final SalesRoleRepository salesRoleRepository;

    @Autowired
    public UserMapperDecorator(@Qualifier("delegate") UserMapper delegate,
                               SalesRoleRepository salesRoleRepository) {
        this.delegate = delegate;
        this.salesRoleRepository = salesRoleRepository;
    }

    @Override
    public User userDtoToUser(UserDTO userDTO) {
        User user = delegate.userDtoToUser(userDTO);

        if(userDTO.getSalesRoleId() != null) {
            user.setSalesRole(salesRoleRepository.findById(userDTO.getSalesRoleId()).orElse(null));
        }
        return user;
    }
}
