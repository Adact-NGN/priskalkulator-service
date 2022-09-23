package no.ding.pk.service;

import java.util.List;

public interface InMemoryCache<K, T, V> {
    void put(K groupKey, T objectKey, V value);
    V get(K groupKey, T objectKey);
    List<V> getAll(K groupKey);
    void remove(K groupKey, T objectKey);
    int size(K groupKey);
    void cleanUp();
    boolean isExpired();
}
