package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.ding.pk.domain.User;
import no.ding.pk.service.UserAzureAdService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.web.client.UserDTO;
import no.ding.pk.web.mappers.MapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdUserController", description = "Controller enabling communication with Azure AD.")
@RestController
@RequestMapping("/api/ad/users")
public class AdUserController {

    private static final Logger log = LoggerFactory.getLogger(AdUserController.class);

    private final UserAzureAdService adService;
    private final UserService userService;
    private final MapperService mapperService;

    @Autowired
    public AdUserController(UserAzureAdService adService, UserService userService, MapperService mapperService) {
        this.adService = adService;
        this.userService = userService;
        this.mapperService = mapperService;
    }

    /**
     * Get User informatjon from AD by user e-mail.
     * @param email The e-mail to identify the user in AD.
     * @return UserDTO object if fount in AD, else null.
     */
    @Operation(summary = "Get User information from AD by user e-mail",
            method = "GET",
            parameters = {
                    @Parameter(name = "email", description = "User email to look up.", required = true)
            },
            tags = {"AdUserController"})
    @GetMapping(path = "/mail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getAdUserByMail(@PathVariable("email") String email) {
        log.debug("Starting request to AD for user with email: " + email);
        User adUser = adService.getUserByEmail(email);

        if(adUser == null) {
            return null;
        }

        User user = userService.findByEmail(adUser.getEmail());
        adUser.setSalesRole(user.getSalesRole());

        return mapperService.toUserDTO(adUser);
    }

    /**
     * Search for user with partial or complete email address.
     * @param email Partial or complete email address to use in the search.
     * @return List of all possible UserDTO object, else empty list of there where no hits.
     */
    @Operation(summary = "Search for users with partial or complete email address.",
            method = "GET",
            parameters = {
                    @Parameter(name = "email", description = "Partial or complete email address to use in the search.", required = true)
            },
            tags = {"AdUserController"})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> searchForUserByEmail(@RequestParam("email") String email) {
        log.debug("Searching for user with email: " + email);

        List<User> adUsers = adService.searchForUserByEmail(email);

        List<String> adUserEmails = adUsers.stream().map(User::getEmail).toList();

        List<User> users = userService.findByEmailInList(adUserEmails);

        adUsers.forEach(adUser -> {
            users.stream().filter(user -> user.getEmail().equals(adUser.getEmail())).findFirst()
                    .ifPresent(filteredUser -> adUser.setSalesRole(filteredUser.getSalesRole()));
        });

        return mapperService.toUserDTOList(adUsers);
    }
    
}
