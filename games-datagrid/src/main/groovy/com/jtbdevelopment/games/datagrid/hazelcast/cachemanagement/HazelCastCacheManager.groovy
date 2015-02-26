package com.jtbdevelopment.games.datagrid.hazelcast.cachemanagement

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * Date: 2/25/15
 * Time: 7:13 AM
 */
@Component
@CompileStatic
class HazelCastCacheManager implements CacheManager {
    @Autowired
    HazelcastInstance hazelCastInstance

    private ConcurrentHashMap<String, HazelCastCache> caches = new ConcurrentHashMap<>()

    @Override
    Cache getCache(final String name) {
        if (!caches.containsKey(name)) {
            IMap map = hazelCastInstance.getMap(name)
            if (map == null) {
                throw new IllegalStateException('Could not get hazelcast map')
            }
            HazelCastCache wrapper = new HazelCastCache(name, map)
            caches.putIfAbsent(name, wrapper)
        }
        return caches[name]
    }

    @Override
    Collection<String> getCacheNames() {
        return caches.keySet()
    }
}
