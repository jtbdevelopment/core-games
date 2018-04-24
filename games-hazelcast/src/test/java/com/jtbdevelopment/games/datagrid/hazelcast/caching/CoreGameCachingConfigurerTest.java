package com.jtbdevelopment.games.datagrid.hazelcast.caching;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.jtbdevelopment.games.dao.caching.CacheConstants;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 3/7/15 Time: 4:17 PM
 */
public class CoreGameCachingConfigurerTest {

  @Test
  public void testModifyConfiguration() {
    Config config = Mockito.mock(Config.class);
    new CoreGameCachingConfigurer().modifyConfiguration(config);
    Mockito.verify(config).addMapConfig(new MapConfig() {{
      setName(CacheConstants.PLAYER_S_AND_SID_CACHE);
      setMaxIdleSeconds(300);
    }});
    Mockito.verify(config).addMapConfig(new MapConfig() {{
      setName(CacheConstants.PLAYER_MD5_CACHE);
      setMaxIdleSeconds(300);
    }});
    Mockito.verify(config).addMapConfig(new MapConfig() {{
      setName(CacheConstants.PLAYER_ID_CACHE);
      setMaxIdleSeconds(300);
    }});
    Mockito.verify(config).addMapConfig(new MapConfig() {{
      setName(CacheConstants.GAME_ID_CACHE);
      setMaxIdleSeconds(300);
    }});
  }

}
