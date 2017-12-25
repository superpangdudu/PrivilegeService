package com.plus3.privilege.common;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2017/12/15.
 */
public class ObjectCacheMap<K, V> {
    private Map<K, V> cacheMap = new ConcurrentHashMap<>();

    //===================================================================================
    public V get(K key) {
        return cacheMap.get(key);
    }

    public void put(K key, V value) {
        cacheMap.put(key, value);
    }

    public V remove(K key) {
        return cacheMap.remove(key);
    }

    public Collection<V> values() {
        return cacheMap.values();
    }

    public Collection<K> keys() {
        return cacheMap.keySet();
    }

    public void clear() {
        cacheMap.clear();
    }
}
