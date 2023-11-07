package no.ding.pk.config;

import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.service.UserAzureAdService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.enums.SalesRoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

@Profile("prod")
@Component
public class StartupProd {

    private static final Logger log = LoggerFactory.getLogger(StartupProd.class);

    private final UserAzureAdService userAzureAdService;
    private final UserService userService;
    private final SalesRoleService salesRoleService;

    @Autowired
    public StartupProd(UserAzureAdService userAzureAdService, UserService userService, SalesRoleService salesRoleService) {
        this.userAzureAdService = userAzureAdService;
        this.userService = userService;
        this.salesRoleService = salesRoleService;
    }

    @Transactional
    @PostConstruct
    public void postConstruct() {
        log.info("Prod startup...");
        List<String> superAdmins = List.of(
                "Eirik.Flaa@ngn.no",
                "kjetil.torvund.minde@ngn.no",
                "thomas.nilsen@ngn.no"
        );

        log.info("Getting and creating admin users");
        for(String userEmail : superAdmins) {
            User superAdminUser = userService.findByEmail(userEmail);

            if(superAdminUser == null) {
                superAdminUser = userAzureAdService.getUserByEmail(userEmail);

                if (superAdminUser != null) {
                    superAdminUser = userService.save(superAdminUser, null);
                }
            }

            SalesRole admin = salesRoleService.findSalesRoleByRoleName(SalesRoleName.Superadmin.name());

            if(admin != null && admin.getUserList() != null && !admin.getUserList().contains(superAdminUser)) {
                admin.addUser(superAdminUser);
                salesRoleService.save(admin);
            }
        }

        List<User> adminUsers = userService.findByEmailInList(superAdmins);

        log.debug("Finished with prod startup, created {} amount of admin users.", adminUsers.size());
    }
}
