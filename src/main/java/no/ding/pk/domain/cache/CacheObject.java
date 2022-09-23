package no.ding.pk.domain.cache;

public class CacheObject<V> {
    public long lastAccessed = System.currentTimeMillis();
    public V value;
    
    public CacheObject() {
    }
    
    protected CacheObject(V value) {
        this.value = value;
    }
}
