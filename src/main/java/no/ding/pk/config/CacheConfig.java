package no.ding.pk.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import no.ding.pk.config.cache.PriceOffersWithStatusKeyGenerator;
import no.ding.pk.config.cache.SapMaterialKeyGenerator;
import no.ding.pk.config.cache.StandardPriceKeyGenerator;
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
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .initialCapacity(500000)
                .recordStats();

        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
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

    @Bean("priceOffersWithStatusKeyGenerator")
    public KeyGenerator priceOffersWithStatusKeyGenerator() {
        return new PriceOffersWithStatusKeyGenerator();
    }

}
