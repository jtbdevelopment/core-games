package com.jtbdevelopment.games.mongo.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.Player;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 2/8/15 Time: 3:49 PM
 */
public class MongoPlayerJacksonRegistrationTest {

  @Test
  public void testCustomizeModule() {
    MongoPlayerJacksonRegistration registration = new MongoPlayerJacksonRegistration();
    SimpleModule module = Mockito.mock(SimpleModule.class);
    registration.customizeModule(module);
    Mockito.verify(module).addAbstractTypeMapping(Player.class, MongoPlayer.class);
  }

}
