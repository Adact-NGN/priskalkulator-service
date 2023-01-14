package no.ding.pk.web.controllers.v1;

import no.ding.pk.service.sap.SapMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/material")
public class MaterialController {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    private final SapMaterialService service;

    @Autowired
    public MaterialController(@Qualifier("sapMaterialServiceImpl") SapMaterialService service) {
        this.service = service;
    }
}
