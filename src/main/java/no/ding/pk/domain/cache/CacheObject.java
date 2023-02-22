package no.ding.pk.domain.cache;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CacheObject<V> {
    private String key;
    private V value;
    private static int objectCounter = 0;
    public long accessed;

    public CacheObject<V> next;
    public CacheObject<V> prev;
    
    public CacheObject() {
    }
    
    public CacheObject(V value) {
        this.accessed = System.currentTimeMillis();
        this.value = value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void incrementObjectCounter() {
        objectCounter++;
    }

    public static <V> CacheObject<V> get(V data) {
        objectCounter++;
        return new CacheObject<>(data);
    }

    public V getValue() {
        objectCounter++;
        accessed = System.currentTimeMillis();
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CacheObject other = (CacheObject) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public void setLastAccessed(long currentTimeMillis) {
    }

    
}
