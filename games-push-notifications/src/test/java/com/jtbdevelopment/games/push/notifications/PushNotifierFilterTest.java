package com.jtbdevelopment.games.push.notifications;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.push.PushWorthyFilter;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 10/11/2015 Time: 8:36 PM
 */
public class PushNotifierFilterTest {

  private IMap map = Mockito.mock(IMap.class);
  private HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractMultiPlayerGameRepository gameRepository = Mockito
      .mock(AbstractMultiPlayerGameRepository.class);
  private PushNotifier pushNotifier = Mockito.mock(PushNotifier.class);
  private PushWorthyFilter pushWorthyFilter = Mockito.mock(PushWorthyFilter.class);
  private PushNotifierFilter filter = new PushNotifierFilter();
  private String pid = "XY12";
  private String gid = "113";
  private StringMPGame game = new StringMPGame();
  private StringPlayer player = new StringPlayer();

  @Before
  public void setup() {

    game.setId(gid);
    player.setId(pid);
    Mockito.when(hazelcastInstance.getMap(PushNotifierFilter.getPLAYER_PUSH_TRACKING_MAP()))
        .thenReturn(map);
    filter.hazelcastInstance = hazelcastInstance;
    filter.gameRepository = gameRepository;
    filter.playerRepository = playerRepository;
    filter.pushNotifier = pushNotifier;
    filter.filter = pushWorthyFilter;
    filter.setup();

    Assert.assertSame(map, filter.recentlyPushedPlayers);
  }

  @Test
  public void testDoesNothingIfValueOnEvictionIsTrue() {
    filter.entryEvicted(makeEntryEvent(true));
    Mockito.verify(pushNotifier, Mockito.never()).notifyPlayer(Matchers.any(), Matchers.any());
  }

  private EntryEvent<GamePublicationTracker, Boolean> makeEntryEvent(boolean oldValue) {
    GamePublicationTracker tracker = new GamePublicationTracker();
    tracker.setPid(pid);
    tracker.setGid(gid);
    return new EntryEvent<>("TEST", null, 0, tracker, oldValue,
        null);
  }

  private EntryEvent<GamePublicationTracker, Boolean> makeEntryEvent() {
    return makeEntryEvent(false);
  }

  @Test
  public void testDoesNothingIfRecentlyPushedToPlayer() {
    Mockito.when(map.putIfAbsent(pid, pid)).thenReturn(pid);
    filter.recentlyPushedPlayers.put(pid, pid);
    filter.entryEvicted(makeEntryEvent());
    Mockito.verify(pushNotifier, Mockito.never()).notifyPlayer(Matchers.any(), Matchers.any());
  }

  @Test
  public void testDoesNothingIfUnableToLoadPlayer() {
    Mockito.when(playerRepository.findById(pid)).thenReturn(Optional.empty());
    Mockito.when(gameRepository.findById(gid)).thenReturn(Optional.of(game));
    filter.entryEvicted(makeEntryEvent());
    Mockito.verify(pushNotifier, Mockito.never()).notifyPlayer(Matchers.any(), Matchers.any());
  }

  @Test
  public void testDoesNothingIfUnableToLoadGame() {
    Mockito.when(playerRepository.findById(pid)).thenReturn(Optional.of(player));
    Mockito.when(gameRepository.findById(gid)).thenReturn(Optional.empty());
    filter.entryEvicted(makeEntryEvent());
    Mockito.verify(pushNotifier, Mockito.never()).notifyPlayer(Matchers.any(), Matchers.any());
  }

  @Test
  public void testDoesNothingIfNotPushWorthy() {
    Mockito.when(playerRepository.findById(pid)).thenReturn(Optional.of(player));
    Mockito.when(gameRepository.findById(gid)).thenReturn(Optional.of(game));
    Mockito.when(pushWorthyFilter.shouldPush(player, game)).thenReturn(false);
    filter.entryEvicted(makeEntryEvent());
    Mockito.verify(pushNotifier, Mockito.never()).notifyPlayer(Matchers.any(), Matchers.any());
  }

  @Test
  public void testDoesPushIfPushWorthy() {
    Mockito.when(playerRepository.findById(pid)).thenReturn(Optional.of(player));
    Mockito.when(gameRepository.findById(gid)).thenReturn(Optional.of(game));
    Mockito.when(pushWorthyFilter.shouldPush(player, game)).thenReturn(true);
    filter.entryEvicted(makeEntryEvent());
    Mockito.verify(pushNotifier).notifyPlayer(player, game);
  }
}
