package org.codeworks.dsp.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by benjaminkc on 16/12/11.
 */
@Component
public class EhcacheUtil {

    @Autowired
    private CacheManager manager;

    public EhcacheUtil(){}

    public void put(String cacheName, String key, Object value) {
        Cache cache = manager.getCache(cacheName);
        if (cache==null) return;
        Element element = new Element(key, value);
        cache.put(element);
    }

    public Object get(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        if (cache==null) return null;
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue();
    }

    public Cache get(String cacheName) {
        return manager.getCache(cacheName);
    }

    public void remove(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        cache.remove(key);
    }

    public void removeAll(){
        manager.removeAllCaches();
    }

    public void removeCache(String cacheName){
        manager.removeCache(cacheName);
    }
}
