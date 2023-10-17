package no.ding.pk.config;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Value("${cache.max.amount.items:5000}") 
    private Integer capacity;

    @Bean
    public InMemory3DCache<String, String, MaterialStdPriceDTO> standardPriceInMemoryCache() {
        return new PingInMemory3DCache<>(capacity);
    }

    @Bean
    public InMemory3DCache<String, String, MaterialDTO> materialInMemoryCache() {
        return new PingInMemory3DCache<>(capacity);
    }

    @Bean
    public InMemory3DCache<String, String, MaterialPrice> materialPriceCache() {
        return new PingInMemory3DCache<>(capacity);
    }
}
