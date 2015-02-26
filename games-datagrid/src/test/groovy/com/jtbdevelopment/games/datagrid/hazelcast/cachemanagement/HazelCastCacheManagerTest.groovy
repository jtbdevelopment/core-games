package com.jtbdevelopment.games.datagrid.hazelcast.cachemanagement

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.springframework.cache.Cache

/**
 * Date: 2/25/15
 * Time: 7:09 PM
 */
class HazelCastCacheManagerTest extends GroovyTestCase {
    HazelCastCacheManager manager = new HazelCastCacheManager()

    public void testGetsNewMap() {
        String name = 'named'
        IMap map = [] as IMap
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert name == n
                        return map
                }
        ] as HazelcastInstance

        Cache c = manager.getCache(name)
        assert c
        assert c instanceof HazelCastCache
        assert c.nativeCache.is(map)
    }

    public void testRepeatMapGets() {
        String name = 'named'
        IMap map = [] as IMap
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert name == n
                        return map
                }
        ] as HazelcastInstance

        Cache c = manager.getCache(name)
        assert c
        assert c instanceof HazelCastCache
        assert c.nativeCache.is(map)
        assert c.is(manager.getCache(name))
        assert c.is(manager.getCache(name))
        assert c.is(manager.getCache(name))
    }

    void testGetMapNames() {
        def names = ['name1', 'name2', 'name3'] as Set
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert names.contains(n)
                        return [] as IMap
                }
        ] as HazelcastInstance
        names.each { manager.getCache(it) }

        assert manager.cacheNames as Set == names
    }
}
