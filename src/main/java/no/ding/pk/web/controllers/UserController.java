package no.ding.pk.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.ding.pk.domain.User;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.v2.web.client.UserDTO;
import no.ding.pk.web.handlers.UserNotFoundException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/api/users", "/api/v1/users"})
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, @Qualifier("modelMapper") ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }
    
    /**
     * List all users
     * @return A list of all users, else empty.
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> list() {
        List<User> userList = userService.findAll();
        
        if(!userList.isEmpty()) {
            return userList.stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
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
    public UserDTO getById(@PathVariable("id") Long id) {
        log.debug("Got id: " + id);
        if(id != null) {
            Optional<User> optUser = userService.findById(id);
            if(optUser.isEmpty()) {
                log.info("Could not find a user with id: " + id);
                return null;
            }
            User user = optUser.get();

            return modelMapper.map(user, UserDTO.class);
        }
        return null;
    }
    
    /**
     * Create new User object.
     * @param userDTO User DTO object with values
     * @return Newly created User object.
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User tempUser = modelMapper.map(userDTO, User.class);
        log.debug("Where we able to map the SalesRole: {}", tempUser.getSalesRole() != null);
        User createdUser = userService.save(tempUser, null);
        
        log.debug("Created new user with id: " + createdUser.getId());
        if(createdUser.getSalesRole() != null) {
            log.debug("User has sales role with id: {}", createdUser.getSalesRole().getId());
        } else {
            log.debug("User has not been assigned a Sales Role");
        }

        return modelMapper.map(createdUser, UserDTO.class);
    }

    /**
     * Update existing user with new values.
     *
     * @param id      User ID.
     * @param userDTO New values to be set on the existing User object.
     * @return Updated User object.
     * @throws JsonProcessingException thorwn when proccessing of JSON fails.
     */
    @PutMapping(path = "/save/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO save(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) throws JsonProcessingException {
        log.debug("Trying to update a user with id: " + id);

        if(id == null) {
            log.error("Put request was given non existing user to update.");
            return null;
        }
        
        log.debug("Updating User with id: " + id);
        
        Optional<User> result = userService.findById(id);
        
        log.debug("Found user: " + result.isPresent());
        
        if(result.isEmpty()) {
            log.error("User with ID {} was not found", id);
            return null;
        }

        User mappedUser = modelMapper.map(userDTO, User.class);
        log.debug("DTO to entity mapping results: {}", mappedUser);
        User updatedUser = userService.save(mappedUser, id);
        log.debug("Persisted user {}", updatedUser);

        return modelMapper.map(updatedUser, UserDTO.class);
    }
    
    /**
     * Delete User object by ID
     * @param id The User ID for the user to be removed.
     * @return True if User object was found and deleted, else false.
     */
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * Get user by email
     * @param email email address to use
     * @return A UserDTO object if any found, else Exception
     */
    @GetMapping(path = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUserByEmail(@PathVariable("email") String email) {
        log.debug("Trying to get user by email: {}", email);

        User byEmail = userService.findByEmail(email);

        if(byEmail != null) {
            return modelMapper.map(byEmail, UserDTO.class);
        }

        throw new UserNotFoundException(String.format("User with email: {} not found.", email));
    }
}
