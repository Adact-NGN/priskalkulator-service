package no.ding.pk.web.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.ding.pk.domain.User;
import no.ding.pk.web.dto.AdUserDTO;
import no.ding.pk.web.dto.UserDTO;

@Component
public class MapperService {

    private ModelMapper modelMapper;
    
    @Autowired
    public MapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toUser(AdUserDTO adUserDTO) {
        return modelMapper.map(adUserDTO, User.class);
    }

    public List<User> toUserList(List<AdUserDTO> adUserList) {
        return ((List<User>) adUserList.stream().map(this::toUser).collect(Collectors.toList()));
    }

    public UserDTO toUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
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
        return modelMapper.map(user, User.class);
    }
}
