package no.ding.pk.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import no.ding.pk.config.cache.SapMaterialKeyGenerator;
import no.ding.pk.config.cache.StandardPriceKeyGenerator;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Value("${cache.max.amount.items:5000}") 
    private Integer capacity;

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .initialCapacity(5000)
                .recordStats());
        return caffeineCacheManager;
    }

    @Bean("stdPriceKeyGenerator")
    public KeyGenerator stdPriceKeyGenerator() {
        return new StandardPriceKeyGenerator();
    }

    @Bean("sapMaterialKeyGenerator")
    public KeyGenerator sapMaterialKeyGenerator() {
        return new SapMaterialKeyGenerator();
    }

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
