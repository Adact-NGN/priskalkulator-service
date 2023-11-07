package no.ding.pk.config;

import no.ding.pk.domain.User;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Profile({"test"})
@Configuration
//@PropertySource({
//        "classpath:application.properties",
//        "classpath:application-dev.properties",
//})
public class DevConfig {

    private final Logger log = LoggerFactory.getLogger(DevConfig.class);

    private final UserService userService;

    private final SalesRoleService salesRoleService;

    @Autowired
    public DevConfig(UserService userService, SalesRoleService salesRoleService) {
        this.userService = userService;
        this.salesRoleService = salesRoleService;
    }

    @PostConstruct
    public void startUp() {
        User eirik = userService.findByEmail("Eirik.Flaa@ngn.no");

        if(eirik == null) {
            log.debug("Persisting user: Eirik Flaa");
            eirik = User.builder("Eirik", "Flaa", "Eirik Flaa", "Eirik.Flaa@ngn.no",
                    "Eirik.Flaa@ngn.no")
                    .powerOfAttorneyOA(5)
                    .powerOfAttorneyFA(5)
                    .build();

            userService.save(eirik, null);
        }
    }
}
