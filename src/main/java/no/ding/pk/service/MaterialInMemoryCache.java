package no.ding.pk.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.ding.pk.domain.cache.CacheObject;

@Component
public class MaterialInMemoryCache<K, T, V> implements InMemoryCache<K, T, V>  {
    private final Logger log = LoggerFactory.getLogger(MaterialInMemoryCache.class);
    
    @Value("${cache.ttl:36000000}")
    private long timeToLive; // = 1000 * 60 * 60; // 1 hour
    @Value("${cache.max.amount.items:5000}")
    private int maxItems = 5000;
    
    private Date expires;
    
    private final Map<K, Map<T, CacheObject<V>>> cacheMap;
    
    @Scheduled(fixedDelayString = "${cache.ttl:3600000}")
    public void run() {
        log.debug("Cleaning up the cache..");
        updatedExpirationDate();
        cleanUp();
    }
    
    private void updatedExpirationDate() {
        expires = DateUtils.addMilliseconds(new Date(), Long.valueOf(timeToLive).intValue());
    }
    
    public MaterialInMemoryCache() { 
        log.debug(String.format("Max item cache size: %d", this.maxItems));
        updatedExpirationDate();
        cacheMap = new LinkedHashMap<>(maxItems);
    }
    
    public void put(K groupKey, T objectKey, V value) {
        synchronized (cacheMap) {
            if(!cacheMap.containsKey(groupKey)) {
                cacheMap.put(groupKey, new LinkedHashMap<T, CacheObject<V>>());
            }
            
            CacheObject<V> cacheObject = new CacheObject<>();
            cacheObject.value = value;
            
            if(cacheMap.get(groupKey).containsKey(objectKey)) {
                log.debug(String.format("Group %s already contains objectKey %s", groupKey, objectKey));
            }
            
            if(cacheMap.get(groupKey).containsValue(cacheObject)) {
                log.debug(String.format("Group %s already contains object: %s", groupKey, cacheObject.toString()));
            }
            
            cacheMap.get(groupKey).put(objectKey, cacheObject);
        }
    }
    
    public V get(K groupKey, T key) {
        synchronized (cacheMap) {
            if(!cacheMap.containsKey(groupKey)) {
                return null;
            }
            
            if(!cacheMap.get(groupKey).containsKey(key)) {
                return null;
            }
            
            CacheObject<V> c = cacheMap.get(groupKey).get(key);
            
            if(c == null) {
                return null;
            } else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }
    
    public List<V> getAll(K groupKey) {
        synchronized (cacheMap) {
            List<V> returnList = new ArrayList<>();
            
            for(CacheObject<V> cacheObj : cacheMap.get(groupKey).values()) {
                returnList.add(cacheObj.value);
            }
            
            return returnList;
        }
    }
    
    public void remove(K groupKey, T objectKey) {
        synchronized (cacheMap) {
            if(cacheMap.containsKey(groupKey)) {
                if(cacheMap.get(groupKey).containsKey(objectKey)) {
                    cacheMap.get(groupKey).remove(objectKey);
                }
            }
        }
    }
    
    public int size(K groupKey) {
        synchronized (cacheMap) {
            if(cacheMap.containsKey(groupKey)) {
                log.debug(String.format("Synchronized cache size: %d", cacheMap.get(groupKey).size()));
                return cacheMap.get(groupKey).size();
            }
            return 0;
        }
    }
    
    public boolean isExpired() {
        return expires.getTime() < System.currentTimeMillis();
    }
    
    public void cleanUp() {
        long now = System.currentTimeMillis();
        Map<K, List<T>> groupDeleteKey = null;
        
        
        if(cacheMap.size() > 0) {
            log.debug("Cachemap has size: " + cacheMap.size());
        } else {
            log.debug("Cachemap is empty");
        }
        
        synchronized (cacheMap) {
            Set<K> groupKeySet = cacheMap.keySet();
            log.debug(String.format("Keyset size is %d with content %s", groupKeySet.size(), groupKeySet.toString()));
            groupDeleteKey = new HashMap<>();
            
            log.debug(String.format("Time to live is set to: %d", timeToLive));
            
            for(K key : groupKeySet) {
                Map<T, CacheObject<V>> objectGroup = cacheMap.get(key);
                Set<T> objectGroupKeySet = objectGroup.keySet();
                
                List<T> objectDeleteKey = new ArrayList<>();
                CacheObject<V> c = null;
                for(T objectKey : objectGroupKeySet) {
                    c = objectGroup.get(objectKey);
                    
                    if(c != null && (now > (timeToLive + c.lastAccessed))) {
                        log.debug(String.format("Found expired cached object, last accessed: %s", prettyPrintDate(c.lastAccessed)));
                        objectDeleteKey.add(objectKey);
                    }
                }
                
                groupDeleteKey.put(key, objectDeleteKey);
            }
        }
        
        log.debug(String.format("Cache size before deletion: %d", this.cacheMap.keySet().size()));
        
        for(K key : groupDeleteKey.keySet()) {
            List<T> objectKeySet = groupDeleteKey.get(key);
            synchronized(cacheMap) {
                for(T objectKey : objectKeySet) {
                    log.debug(String.format("Removing cached object with key: %s", objectKey));
                    cacheMap.get(key).remove(objectKey);
                }
                
                cacheMap.remove(key);
            }
        }
        
        log.debug(String.format("Cache size after deletion: %d", this.cacheMap.keySet().size()));
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
    
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
    
}
