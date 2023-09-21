package no.ding.pk.service.cache;

import java.util.List;

import no.ding.pk.domain.cache.CacheObject;

public interface InMemory2DCache<T, V> {
    void put(T objectKey, V value);
    V get(T objectKey);
    void remove(T objectKey);
    int size();
    void cleanUp();
    boolean isExpired();
    List<V> getAll();
}
