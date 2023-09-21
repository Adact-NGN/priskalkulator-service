package no.ding.pk.service.cache;

import no.ding.pk.domain.cache.CacheObject;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class PingInMemory2DCache<T, V> implements InMemory2DCache<T, V> {
    private final Logger log = LoggerFactory.getLogger(PingInMemory2DCache.class);
    
    @Value("${cache.max.amount.items:5000}")
    private int maxItems = 2;
    
    private final int capacity;
    private int count;
    
    private Date expires;
    
    private final Map<T, CacheObject<V>> map;
    private final CacheObject<V> head;
    private final CacheObject<V> tail;
    
    public PingInMemory2DCache() {
        log.debug("Max item cache size: {}", this.maxItems);
        capacity = maxItems;
        map = new HashMap<>(maxItems);
        head = new CacheObject<>();
        tail = new CacheObject<>();
        head.next = tail;
        tail.prev = head;
        head.prev = null;
        tail.next = null;
        count = 0;
    }
    
    private void deleteNode(CacheObject<V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void addToHead(CacheObject<V> node) {
        node.next = head.next;
        node.next.prev = node;
        node.prev = head;
        head.next = node;
    }
    
    @Override
    public V get(T objectKey) {
        synchronized (map) {
            if(map.get(objectKey) != null) {
                CacheObject<V> c = map.get(objectKey);
                
                deleteNode(c);
                addToHead(c);
                
                return c.getValue();
            }
            
            return null;
        }
    }
    
    @Override
    public void put(T objectKey, V value) {
        synchronized (map) {
            if(map.containsKey(objectKey)) {
                CacheObject<V> node = map.get(objectKey);
                node.setValue(value);
                
                deleteNode(node);
                addToHead(node);
                log.debug(String.format("ObjectKey %s already exists", objectKey));
                return;
            } else {
                CacheObject<V> cacheObject = new CacheObject<>();
                cacheObject.setValue(value);

                map.put(objectKey, cacheObject);
                
                if(count < capacity) {
                    count++;
                    addToHead(cacheObject);
                } else {
                    map.remove(tail.prev.getKey());
                    deleteNode(tail.prev);
                    addToHead(cacheObject);
                }
            }
        }
    }
    
    @Override
    public void remove(T objectKey) {
        synchronized (map) {
            map.remove(objectKey);
        }
    }
    
    @Override
    public int size() {
        synchronized (map) {
            return map.size();
        }
    }
    
    @Override
    public void cleanUp() {
        Set<T> objectKeySet = map.keySet();
        synchronized(map) {
            for(T key : objectKeySet) {
                log.debug(String.format("Removing cached object with key: %s", key));
                map.remove(key);
            }
        }
    }
    
    @Override
    public boolean isExpired() {
        return expires.getTime() < System.currentTimeMillis();
    }

    @Override
    public List<V> getAll() {
        return map.values().stream().map(obj -> obj.getValue()).collect(Collectors.toList());
    }
}
