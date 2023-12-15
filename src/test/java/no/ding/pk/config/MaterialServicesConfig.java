package no.ding.pk.config;

import no.ding.pk.repository.offer.MaterialPriceRepository;
import no.ding.pk.repository.offer.MaterialRepository;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.offer.MaterialPriceService;
import no.ding.pk.service.offer.MaterialPriceServiceImpl;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.service.offer.MaterialServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MaterialServicesConfig {
    @Bean
    protected MaterialPriceService materialPriceService(MaterialPriceRepository materialPriceRepository) {
        return new MaterialPriceServiceImpl(materialPriceRepository);
    }

    @Bean
    protected MaterialService materialService(MaterialRepository materialRepository, MaterialPriceService materialPriceService) {
        return new MaterialServiceImpl(materialRepository, materialPriceService);
    }
}
