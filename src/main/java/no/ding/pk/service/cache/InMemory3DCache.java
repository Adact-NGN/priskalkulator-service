package no.ding.pk.service.cache;

import java.util.List;

public interface InMemory3DCache<K, T, V> {
    void put(K groupKey, T objectKey, V value);
    V get(K groupKey, T objectKey);
    List<V> getAllInList(K groupKey);
    void remove(K groupKey, T objectKey);
    int size(K groupKey);
    boolean isExpired();
    boolean contains(K groupKey, T objectKey);

    List<V> getAllInList(K groupKey, List<T> objectKeys);
}
