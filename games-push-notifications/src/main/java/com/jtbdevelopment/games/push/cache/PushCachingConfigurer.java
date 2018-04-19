package com.jtbdevelopment.games.push.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.jtbdevelopment.core.hazelcast.HazelcastConfigurer;
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter;
import com.jtbdevelopment.games.push.websocket.PushWebSocketPublicationListener;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/**
 * Date: 3/6/15 Time: 7:06 PM
 */
@Component
public class PushCachingConfigurer implements HazelcastConfigurer {

  private static final int ONE_MINUTE = 60;

  @Override
  public void modifyConfiguration(final Config config) {
    Arrays.asList(
        PushNotifierFilter.getPLAYER_PUSH_TRACKING_MAP(),
        PushWebSocketPublicationListener.getWEB_SOCKET_TRACKING_MAP()).forEach(cache -> {

      MapConfig mc = new MapConfig(cache);
      mc.setMaxIdleSeconds(ONE_MINUTE);
      config.addMapConfig(mc);
    });
  }
}
