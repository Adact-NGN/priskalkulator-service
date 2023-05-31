package no.ding.pk.service.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import no.ding.pk.domain.cache.CacheObject;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.service.sap.SapMaterialService;

@Component
public class PingInMemory3DCache<K, T, V> implements InMemory3DCache<K, T, V> {
    private final Logger log = LoggerFactory.getLogger(PingInMemory3DCache.class);
    
    private int capacity;
    
    private Date expires;

    // private final Map<K, InMemory2DCache<T, CacheObject<V>>> cacheMap;
    private final Map<K, Cache<T, CacheObject<V>>> cacheMap;
    
    public PingInMemory3DCache(@Value("${cache.max.amount.items:5000}") Integer capacity) {
        this.capacity = capacity;
        log.debug("Max item cache size: {}", this.capacity);
        // cacheMap = new HashMap<K, InMemory2DCache<T, CacheObject<V>>>();
        cacheMap = new HashMap<K, Cache<T, CacheObject<V>>>();
    }
    
    public void put(K groupKey, T objectKey, V value) {
        synchronized (cacheMap) {
            if(!cacheMap.containsKey(groupKey)) {
                // InMemory2DCache<T, CacheObject<V>> cache = new PingInMemory2DCache<>();
                Cache<T, CacheObject<V>> caffeine = Caffeine.newBuilder()
                .maximumSize(capacity)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
                
                cacheMap.put(groupKey, caffeine);
            }
            
            CacheObject<V> cacheObject;
            if(cacheMap.get(groupKey).getIfPresent(objectKey) != null) {
                if(cacheMap.get(groupKey).getIfPresent(objectKey).getValue().equals(value)) {
                    log.debug("Group {} already contains objectKey {} and given object.", groupKey, objectKey);
                    return;
                }

                cacheObject = cacheMap.get(groupKey).getIfPresent(objectKey);
            } else {
                cacheObject = new CacheObject<>();
            }
            
            cacheObject.setValue(value);
            
            if(cacheMap.get(groupKey).getIfPresent(objectKey) != null) {
                cacheMap.get(groupKey).put(objectKey, cacheObject);
                log.debug("Group {} already contains object: {}, replacing!", groupKey, cacheObject);
            } else {
                cacheMap.get(groupKey).put(objectKey, cacheObject);
            }
        }
    }

    public V get(K groupKey, T key) {
        synchronized (cacheMap) {
            if(!cacheMap.containsKey(groupKey) || cacheMap.get(groupKey).getIfPresent(key) == null) {
                return null;
            }
            
            CacheObject<V> c = cacheMap.get(groupKey).getIfPresent(key);
            c.accessed++;
            
            c.setLastAccessed(System.currentTimeMillis());
            return c.getValue();
        }
    }
    
    public List<V> getAllInList(K groupKey) {
        synchronized (cacheMap) {
            if(cacheMap.get(groupKey) != null) {
                List<CacheObject<V>> all = cacheMap.get(groupKey).asMap().entrySet().stream().map(obj -> obj.getValue()).collect(Collectors.toList());

                if(!all.isEmpty()) {
                    return all.stream().map(obj -> obj.getValue()).collect(Collectors.toList());
                }
            }
            
            return new ArrayList<>();
        }
    }
    
    public void remove(K groupKey, T objectKey) {
        synchronized (cacheMap) {
            if(cacheMap.containsKey(groupKey)) {
                cacheMap.get(groupKey).invalidate(objectKey);
            }
        }
    }
    
    public int size(K groupKey) {
        synchronized (cacheMap) {
            if(cacheMap.containsKey(groupKey)) {
                log.debug(String.format("Synchronized cache size: %d", cacheMap.get(groupKey).estimatedSize()));
                return (int) cacheMap.get(groupKey).estimatedSize();
            }
            return 0;
        }
    }
    
    public boolean isExpired() {
        return expires != null && expires.getTime() < System.currentTimeMillis();
    }
    
    @Override
    public boolean contains(K groupKey, T objectKey) {
        synchronized (cacheMap) {
            
            if(cacheMap.containsKey(groupKey)) {
                return cacheMap.get(groupKey).getIfPresent(objectKey) != null;
            }
            
            return false;
        }
    }
    
    @Override
    public List<V> getAllInList(K groupKey, List<T> objectKeys) {
        List<V> returnList = new ArrayList<>();

        if(cacheMap.containsKey(groupKey)) {
            for(T objectKey : objectKeys) {
                @Nullable
                CacheObject<V> ifPresent = cacheMap.get(groupKey).getIfPresent(objectKey);
                if(ifPresent != null && ifPresent.getValue() != null)
                    returnList.add(ifPresent.getValue());
            }
        }
        
        return returnList;
    }
    
    private String prettyPrintDate(long lastAccessed) {
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTimeInMillis(lastAccessed);
        
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSeconds = calendar.get(Calendar.SECOND);
        return String.format("%d.%d.%d - %d:%d:%d", mYear, mMonth, mDay, mHour, mMinute, mSeconds);
    }
    
    public void setCapacity(int maxItems) {
        this.capacity = maxItems;
    }
    
}
