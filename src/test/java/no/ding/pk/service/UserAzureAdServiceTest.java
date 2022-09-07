package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import no.ding.pk.domain.User;

@Ignore
@Profile("itest")
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserAzureAdServiceTest {

    @Autowired
    UserAzureAdServiceImpl uaas;

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
