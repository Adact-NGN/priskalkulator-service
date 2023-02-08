package no.ding.pk.web.mappers;

import java.util.List;
import java.util.stream.Collectors;

import no.ding.pk.web.dto.azure.ad.AdUserDTO;
import no.ding.pk.web.dto.web.client.SalesRoleDTO;
import no.ding.pk.web.dto.web.client.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;

@Component
public class MapperService {

    private final ModelMapper modelMapper;

    @Autowired
    public MapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toUser(AdUserDTO adUserDTO) {
        return modelMapper.map(adUserDTO, User.class);
    }

    public List<User> toUserList(List<AdUserDTO> adUserList) {
        return adUserList.stream().map(this::toUser).collect(Collectors.toList());
    }

    public UserDTO toUserDTO(User user) {
        if(user.getSalesRole() != null) {
            return toUserDTO(user, user.getSalesRole());
        }
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO toUserDTO(User user, SalesRole salesRole) {
        user.setSalesRole(null);
        UserDTO result = modelMapper.map(user, UserDTO.class);
        SalesRoleDTO salesRoleDTO =  modelMapper.map(salesRole, SalesRoleDTO.class);
        result.setSalesRole(salesRoleDTO);
        
        return result;
    }

    public List<UserDTO> toUserDTOList(List<User> userList) {
        return ((List<UserDTO>) userList.stream().map(this::toUserDTO).collect(Collectors.toList()));
    }

    public AdUserDTO toAdUserDto(User user) {
        return modelMapper.map(user, AdUserDTO.class);
    }

    public List<AdUserDTO> toAdUserDTOList(List<User> userList) {
        return ((List<AdUserDTO>) userList.stream().map(this::toAdUserDto).collect(Collectors.toList()));
    }

    public User toUser(UserDTO user) {
        User mappedUser = modelMapper.map(user, User.class);

        return mappedUser;
    }

    public SalesRoleDTO toSalesRoleDTO(SalesRole salesRole) {
        return modelMapper.map(salesRole, SalesRoleDTO.class);
    }
}
