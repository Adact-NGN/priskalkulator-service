package no.ding.pk.web.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.domain.User;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.web.dto.UserDTO;
import no.ding.pk.web.mappers.MapperService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);
    
    private UserRepository userRepository;
    private MapperService mapperService;
    
    @Autowired
    public UserController(UserRepository userRepository, MapperService mapperService) {
        this.userRepository = userRepository;
        this.mapperService = mapperService;
    }

    @GetMapping(path = "/list", produces = "application/json")
    public List<UserDTO> list() {
        List<User> userList = userRepository.findAll();

        return mapperService.toUserDTOList(userList);
    }

    @GetMapping(path = "/id/{id}", produces = "application/json")
    public UserDTO getById(@PathVariable("id") Long id) {
        log.debug("Got id: " + id);
        if(id != null) {
            User user = userRepository.findUserWithSalesRole(id);
            if(user == null) {
                log.info("Could not find a user with id: " + id);
                return null;
            }
            return mapperService.toUserDTO(user);
        }
        return null;
    }

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User tempUser = mapperService.toUser(userDTO);
        User createdUser = userRepository.save(tempUser);

        log.debug("Creating new user with id: " + createdUser.getId());

        return mapperService.toUserDTO(createdUser);
    }

    @PutMapping(path = "/save/{id}", consumes = "application/json", produces = "application/json")
    public UserDTO save(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        

        if(id == null) {
            return null;
        }

        log.debug("Updating User with id: " + id);

        Optional<User> result = userRepository.findById(id);

        log.debug("Found user: " + result.isPresent());

        if(!result.isPresent()) {
            return null;
        }

        User currentUser = null;

        if(result.isPresent()) {
            currentUser = result.get();
        }

        return mapperService.toUserDTO(currentUser);
    }
}
