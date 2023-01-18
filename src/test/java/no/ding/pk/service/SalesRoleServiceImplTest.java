package no.ding.pk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/h2-db.properties")
class SalesRoleServiceImplTest {

    @Autowired
    private SalesRoleService salesRoleService;


}