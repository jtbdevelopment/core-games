package com.jtbdevelopment.games.push.websocket;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.jtbdevelopment.games.push.PushProperties;
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker;
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 10/11/2015 Time: 8:59 PM
 */
public class PushWebSocketPublicationListenerTest {

  private PushProperties pushProperties = Mockito.mock(PushProperties.class);
  private IMap map = Mockito.mock(IMap.class);
  private HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
  private PushNotifierFilter pushNotifierFilter = Mockito.mock(PushNotifierFilter.class);
  private String playerId = "345";
  private String gameId = "543";
  private StringPlayer player = GameCoreTestCase.makeSimplePlayer(playerId);
  private StringMPGame game = GameCoreTestCase.makeSimpleMPGame(gameId);
  private GamePublicationTracker tracker = new GamePublicationTracker();
  private PushWebSocketPublicationListener listener;

  @Before
  public void setUp() throws Exception {
    tracker.setGid(gameId);
    tracker.setPid(playerId);
    Mockito.when(pushProperties.isEnabled()).thenReturn(true);
    PushWebSocketPublicationListener.computeRegistrationCutoff();
    Mockito.when(hazelcastInstance.getMap("PUSH_TRACKING_MAP")).thenReturn(map);
    listener = new PushWebSocketPublicationListener(hazelcastInstance, pushNotifierFilter,
        pushProperties);
  }

  @Test
  public void testSetupWithEnabledPushProperties() {
    Mockito.verify(map).addEntryListener(pushNotifierFilter, true);
  }

  @Test
  public void testSetupWithDisabledPushProperties() {
    Mockito.reset(pushProperties);
    Mockito.reset(map);
    Mockito.when(pushProperties.isEnabled()).thenReturn(false);
    listener = new PushWebSocketPublicationListener(hazelcastInstance, pushNotifierFilter,
        pushProperties);
    Mockito.verify(map, Mockito.never()).addEntryListener(pushNotifierFilter, true);
  }

  @Test
  public void testIgnoresPlayerWithNoDevices() {
    listener.publishedGameUpdateToPlayer(player, game, true);
    Mockito.verify(map, Mockito.never()).put(Matchers.any(), Matchers.any());
    Mockito.verify(map, Mockito.never()).putIfAbsent(Matchers.any(), Matchers.any());
  }

  @Test
  public void testIgnoresPlayerWithNoDevicesRegistedInLast30Days() {
    RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID("X");
    device.setLastRegistered(
        ZonedDateTime.now(ZoneId.of("GMT")).minusDays(30).minusSeconds(60).toInstant());
    player.updateRegisteredDevice(device);
    listener.publishedGameUpdateToPlayer(player, game, true);
    Mockito.verify(map, Mockito.never()).put(Matchers.any(), Matchers.any());
    Mockito.verify(map, Mockito.never()).putIfAbsent(Matchers.any(), Matchers.any());
  }

  @Test
  public void testSetsValueForTruePublishPuts() {
    RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID("X");
    player.updateRegisteredDevice(device);
    listener.publishedGameUpdateToPlayer(player, game, true);
    Mockito.verify(map).put(Matchers.eq(tracker), Matchers.eq(true));
  }

  @Test
  public void testSetsValueForFalsePublishPutsIfAbsent() {
    RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID("X");
    player.updateRegisteredDevice(device);
    listener.publishedGameUpdateToPlayer(player, game, false);
    Mockito.verify(map).putIfAbsent(Matchers.eq(tracker), Matchers.eq(false));
  }

  @Test
  public void testDoesNotSetsValueForFalsePublishWhenPushDisabled() {
    Mockito.reset(pushProperties);
    Mockito.when(pushProperties.isEnabled()).thenReturn(false);
    RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID("X");
    player.updateRegisteredDevice(device);
    listener.publishedGameUpdateToPlayer(player, game, false);
    Mockito.verify(map, Mockito.never()).put(Matchers.any(), Matchers.any());
    Mockito.verify(map, Mockito.never()).putIfAbsent(Matchers.any(), Matchers.any());
  }
}
