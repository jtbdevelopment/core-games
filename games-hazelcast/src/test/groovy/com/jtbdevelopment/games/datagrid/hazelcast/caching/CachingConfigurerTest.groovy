package com.jtbdevelopment.games.datagrid.hazelcast.caching

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.jtbdevelopment.games.dao.caching.CacheConstants

/**
 * Date: 3/7/15
 * Time: 4:17 PM
 */
class CachingConfigurerTest extends GroovyTestCase {
    void testModifyConfiguration() {
        def configs = [CacheConstants.PLAYER_S_AND_SID_CACHE,
                       CacheConstants.PLAYER_ID_CACHE,
                       CacheConstants.PLAYER_MD5_CACHE,
                       CacheConstants.GAME_ID_CACHE] as Set
        def config = [
                addMapConfig: {
                    MapConfig mc ->
                        assert configs.contains(mc.name)
                        configs.remove(mc.name)
                        assert mc.maxIdleSeconds == 300
                }
        ] as Config
        new CachingConfigurer().modifyConfiguration(config)
        assert configs.empty
    }
}
