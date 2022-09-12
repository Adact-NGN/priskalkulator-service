package no.ding.pk.web.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.ding.pk.domain.User;
import no.ding.pk.service.UserAzureAdService;
import no.ding.pk.web.dto.UserDTO;
import no.ding.pk.web.mappers.MapperService;

@RestController
@RequestMapping("/api/ad/users")
public class AdUserController {

    private static Logger log = LoggerFactory.getLogger(AdUserController.class);

    private UserAzureAdService adService;
    private MapperService mapperService;

    @Autowired
    public AdUserController(UserAzureAdService adService, MapperService mapperService) {
        this.adService = adService;
        this.mapperService = mapperService;
    }

    @GetMapping(value = "/mail/{email}", produces = "application/json")
    public UserDTO getAdUserByMail(@PathVariable("email") String email) {
        log.debug("Starting request to AD for user with email: " + email);
        User user = adService.getUserByEmail(email);

        if(user == null) {
            return null;
        }

        return mapperService.toUserDTO(user);
    }

    @GetMapping(produces = "application/json")
    public List<UserDTO> searchForUserByEmail(@RequestParam("email") String email) {
        log.debug("Searching for user with email: " + email);

        List<User> searchResults = adService.searchForUserByEmail(email);

        return mapperService.toUserDTOList(searchResults);
    }
    
}
