package com.jtbdevelopment.games.datagrid.hazelcast.caching

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer
import com.jtbdevelopment.games.dao.caching.CacheConstants
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 3/6/15
 * Time: 7:06 PM
 */
@Component
@CompileStatic
class CachingConfigurer implements HazelcastConfigurer {

    private static final int FIVE_MINUTES = 300

    @Override
    void modifyConfiguration(final Config config) {
        [CacheConstants.PLAYER_S_AND_SID_CACHE,
         CacheConstants.PLAYER_ID_CACHE,
         CacheConstants.PLAYER_MD5_CACHE,
         CacheConstants.GAME_ID_CACHE].each {
            String it ->
                MapConfig mc = new MapConfig(it)
                mc.maxIdleSeconds = FIVE_MINUTES
                config.addMapConfig(mc)
        }
    }
}
