package no.ding.pk.service;

import java.util.List;

import no.ding.pk.domain.User;

public interface UserAzureAdService {
    List<User> getUsersList();
    User getUserByEmail(String email);
    List<User> searchForUserByEmail(String email);
}
