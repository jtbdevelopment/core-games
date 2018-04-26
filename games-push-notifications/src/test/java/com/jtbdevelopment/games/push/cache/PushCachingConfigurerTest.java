package com.jtbdevelopment.games.push.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter;
import com.jtbdevelopment.games.push.websocket.PushWebSocketPublicationListener;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 10/11/2015 Time: 8:32 PM
 */
public class PushCachingConfigurerTest {

  @Test
  public void testModifyConfiguration() {
    Config config = Mockito.mock(Config.class);
    new PushCachingConfigurer().modifyConfiguration(config);
    Mockito.verify(config).addMapConfig(new MapConfig() {{
      setName(PushWebSocketPublicationListener.WEB_SOCKET_TRACKING_MAP);
      setMaxIdleSeconds(60);
    }});
    Mockito.verify(config).addMapConfig(new MapConfig() {{
      setName(PushNotifierFilter.PLAYER_PUSH_TRACKING_MAP);
      setMaxIdleSeconds(60);
    }});
  }

}
