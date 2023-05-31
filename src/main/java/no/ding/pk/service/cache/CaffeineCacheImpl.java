package no.ding.pk.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import no.ding.pk.domain.cache.CacheObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

public class CaffeineCacheImpl<V> implements CaffeineCache {
    
    private Cache<String, CacheObject<V>> cache;

    @Value("${cache.max.amount.items:5000}")
    private int maxItems = 5000;

    public CaffeineCacheImpl() {
        cache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(0)
        .build();
    }

    public void put(String key, V value) {
        CacheObject<V> cacheObject = cache.getIfPresent(key);

        if(cacheObject == null) {
            cacheObject = new CacheObject<>();
            // cacheObject.addValue(value);
        }
        
        cache.put(key, cacheObject);
    }

    public V get(String key) {
        CacheObject<V> cacheObject = cache.getIfPresent(key);

        if(cacheObject == null) {
            return null;
        }
        return cacheObject.getValue();
    }

}
