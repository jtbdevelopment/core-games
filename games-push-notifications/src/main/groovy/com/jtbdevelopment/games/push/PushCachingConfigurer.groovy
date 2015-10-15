package com.jtbdevelopment.games.push

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 3/6/15
 * Time: 7:06 PM
 */
@Component
@CompileStatic
class PushCachingConfigurer implements HazelcastConfigurer {

    private static final int ONE_MINUTE = 60

    @Override
    void modifyConfiguration(final Config config) {
        [PushNotifierFilter.PLAYER_PUSH_TRACKING_MAP,
         PushWebSocketPublicationListener.WEBSOCKET_TRACKING_MAP].each {
            String it ->
                MapConfig mc = new MapConfig(it)
                mc.maxIdleSeconds = ONE_MINUTE
                config.addMapConfig(mc)
        }
    }
}
