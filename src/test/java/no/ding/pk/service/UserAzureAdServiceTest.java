package no.ding.pk.service;

import no.ding.pk.domain.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled(value = "Secret expires after 6 month and will invalidate this test suit.")
@SpringBootTest
public class UserAzureAdServiceTest {

    @Autowired
    private UserAzureAdServiceImpl uaas;

    @Test
    void shouldGetUserByEmail() {
        User user = uaas.getUserByEmail("Eirik.Flaa@ngn.no");
        assertNotNull(user);
        assertThat(user.getEmail(), equalTo("Eirik.Flaa@ngn.no"));
    }

    @Test
    void shouldSearchForUserByEmail() {
        String email = "kjetil.torvund.minde@ngn.no";
        List<User> result = uaas.searchForUserByEmail(email);

        assertNotNull(result);
        assertThat(result, hasSize(greaterThan(0)));
        assertThat(result.get(0).getEmail(), equalTo(email));
    }

    @Test
    void shouldGetUsersListFromGraph() {
        List<User> userList = uaas.getUsersList();

        assertNotNull(userList);
        assertThat(userList, hasSize(greaterThan(0)));
    }
}
