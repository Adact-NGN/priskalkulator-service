package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SapMaterialServiceImpl implements SapMaterialService {
    private static final Logger log = LoggerFactory.getLogger(SapMaterialServiceImpl.class);

    private ObjectMapper objectMapper;
    private String materialServiceUrl;

    public SapMaterialServiceImpl(@Autowired ObjectMapper objectMapper, @Value("${sap.api.material.url}") String materialServiceUrl) {
        this.objectMapper = objectMapper;
        this.materialServiceUrl = materialServiceUrl;
    }
}
