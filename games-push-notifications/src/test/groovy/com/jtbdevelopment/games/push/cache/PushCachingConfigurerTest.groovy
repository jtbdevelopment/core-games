package com.jtbdevelopment.games.push.cache

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter
import com.jtbdevelopment.games.push.websocket.PushWebSocketPublicationListener

/**
 * Date: 10/11/2015
 * Time: 8:32 PM
 */
class PushCachingConfigurerTest extends GroovyTestCase {
    void testModifyConfiguration() {
        def configs = [PushWebSocketPublicationListener.WEB_SOCKET_TRACKING_MAP,
                       PushNotifierFilter.PLAYER_PUSH_TRACKING_MAP] as Set
        def config = [
                addMapConfig: {
                    MapConfig mc ->
                        assert configs.contains(mc.name)
                        configs.remove(mc.name)
                        assert mc.maxIdleSeconds == 60
                }
        ] as Config
        new PushCachingConfigurer().modifyConfiguration(config)
        assert configs.empty
    }
}
