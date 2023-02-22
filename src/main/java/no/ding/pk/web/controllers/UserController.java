package no.ding.pk.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.ding.pk.web.dto.web.client.UserDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    
    /**
     * List all users
     * @return A list of all users, else empty.
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> list() {
        List<User> userList = userService.findAll();
        
        if(!userList.isEmpty()) {
            return userList;
        } else {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get user by id.
     * @param id the user id
     * @return User object if found, else null.
     */
    @GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getById(@PathVariable("id") Long id) {
        log.debug("Got id: " + id);
        if(id != null) {
            Optional<User> optUser = userService.findById(id);
            if(!optUser.isPresent()) {
                log.info("Could not find a user with id: " + id);
                return null;
            }
            User user = optUser.get();
            log.debug("Got user: " + user);

            return user;
        }
        return null;
    }
    
    /**
     * Create new User object.
     * @param userDTO User DTO object with values
     * @return Newly created User object.
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User create(@RequestBody UserDTO userDTO) {
        User tempUser = mapperService.toUser(userDTO);
        User createdUser = userService.save(tempUser, null);
        
        log.debug("Created new user with id: " + createdUser.getId());
        if(createdUser.getSalesRole() != null) {
            log.debug("User has sales role with id: " + Long.toString(createdUser.getSalesRole().getId()));
        } else {
            log.debug("User has not been assigned a Sales Role");
        }
        
        return createdUser;
    }
    
    /**
     * Update existing user with new values.
     * @param id User ID.
     * @param userDTO New values to be set on the existing User object.
     * @return Updated User object.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User save(@PathVariable("id") Long id, @RequestBody String userDTO) throws JsonMappingException, JsonProcessingException {
        log.debug("Trying to update a user with id: " + id);
        log.debug("Object data: " + userDTO);
        
        if(id == null) {
            log.error("Put request was given non existing user to update.");
            return null;
        }
        
        log.debug("Updating User with id: " + id);
        
        Optional<User> result = userService.findById(id);
        
        log.debug("Found user: " + result.isPresent());
        
        if(result.isEmpty()) {
            log.error("{} {} {}", "User with ID", id, "was not found");
            return null;
        }
        
        User currentUser = result.get();
        
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating(currentUser);
        User updatedUser = objectReader.readValue(userDTO);
        log.debug("UserDTO: " + userDTO);
        log.debug("updatedUser" + updatedUser);
        
        updatedUser = userService.save(updatedUser, id);
        
        return updatedUser;
    }
    
    /**
     * Delete User object by ID
     * @param id The User ID for the user to be removed.
     * @return True if User object was found and deleted, else false.
     */
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
