package no.ding.pk.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import no.ding.pk.domain.User;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.UserDTO;
import no.ding.pk.web.mappers.MapperService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static Logger log = LoggerFactory.getLogger(UserController.class);
    
    private UserService userService;
    private MapperService mapperService;
    
    @Autowired
    public UserController(UserService userService, MapperService mapperService) {
        this.userService = userService;
        this.mapperService = mapperService;
    }
    
    @GetMapping(path = "/list", produces = "application/json")
    public List<UserDTO> list() {
        List<User> userList = userService.findAll();
        
        if(!userList.isEmpty()) {
            return mapperService.toUserDTOList(userList);
        } else {
            return new ArrayList<>();
        }
    }
    
    @GetMapping(path = "/id/{id}", produces = "application/json")
    public UserDTO getById(@PathVariable("id") Long id) {
        log.debug("Got id: " + id);
        if(id != null) {
            User user = userService.findUserByIdWithSalesRole(id);
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
        User createdUser = userService.save(tempUser);
        
        log.debug("Creating new user with id: " + createdUser.getId());
        
        return mapperService.toUserDTO(createdUser);
    }
    
    @PutMapping(path = "/save/{id}", consumes = "application/json", produces = "application/json")
    public UserDTO save(@PathVariable("id") Long id, @RequestBody String userDTO) throws JsonMappingException, JsonProcessingException {
        log.debug("Trying to update a user with id: " + id);
        log.debug("Object data: " + userDTO);
        
        if(id == null) {
            return null;
        }
        
        log.debug("Updating User with id: " + id);
        
        Optional<User> result = userService.findById(id);
        
        log.debug("Found user: " + result.isPresent());
        
        if(!result.isPresent()) {
            return null;
        }
        
        User currentUser = null;
        
        if(result.isPresent()) {
            currentUser = result.get();
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating(currentUser);
        User updatedUser = objectReader.readValue(userDTO);
        log.debug("Updated object: " + updatedUser.toString());
        log.debug("Existing object: " + currentUser.toString());
        
        log.debug(String.format("Old hash: %s - %s :New hash", System.identityHashCode(currentUser), System.identityHashCode(updatedUser)));
        
        updatedUser = userService.save(updatedUser);
        
        return mapperService.toUserDTO(updatedUser);
    }
    
    @DeleteMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        log.debug("Trying to delete user with id: " + id);

        JSONObject returnJson = new JSONObject();
        if(userService.delete(id)) {
            returnJson.put("status", "200");
            returnJson.put("message", String.format("User with id %d deleted.", id));
            return returnJson.toString();
        }
        
        returnJson.put("status", "500");
        returnJson.put("message", String.format("Could not delete user with id %d.", id));
        return returnJson.toString();
    }
    
}
