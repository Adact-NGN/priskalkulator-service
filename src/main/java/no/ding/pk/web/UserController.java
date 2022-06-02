package no.ding.pk.web;

import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.domain.User;
import no.ding.pk.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/list", produces = "application/json")
    public List<User> list() {
        List<User> userList = userRepository.findAll();

        return userList;
    }

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    public User create(@RequestBody User user) {
        User createdUser = userRepository.save(user);

        return createdUser;
    }

    @PutMapping(path = "/save/:id", consumes = "application/json", produces = "application/json")
    public User save(@PathParam("id") Long id, @RequestBody User user) {

        if(id == null) {
            return null;
        }

        Optional<User> result = userRepository.findById(id);

        User currentUser = null;

        if(result.isPresent()) {
            currentUser = result.get();
        }

        return currentUser;
    }
}
