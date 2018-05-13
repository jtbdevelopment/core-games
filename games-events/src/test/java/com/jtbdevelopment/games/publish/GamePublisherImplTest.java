package com.jtbdevelopment.games.publish;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleMPGame;
import static org.junit.Assert.assertEquals;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 12/8/14 Time: 7:11 PM
 */
public class GamePublisherImplTest {

  private LatchListener listener1 = new LatchListener();
  private LatchListener listener2 = new LatchListener();
  private GamePublisherImpl publisher = new GamePublisherImpl(10,
      Arrays.asList(listener1, listener2));

  @Test
  public void testCreatesExecutorService() throws ExecutionException, InterruptedException {
    final String expectedResult = "RESULT";
    assertEquals(10, ((ThreadPoolExecutor) publisher.service).getCorePoolSize());
    assertEquals(expectedResult, publisher.service.submit((Callable) () -> {
      Thread.sleep(100);
      return expectedResult;
    }).get());
    publisher.service.shutdownNow();
  }

  @Test
  public void testPublishWithNoListeners() {
    StringMPGame g1 = makeSimpleMPGame("1");
    StringMPGame g2 = makeSimpleMPGame("2");

    publisher = new GamePublisherImpl(10, new ArrayList<>());
    publisher.publish(g1, PTWO);
    publisher.publish(g2, PONE);
    publisher.service.shutdown();

    //  Not crashing is success
  }

  @Test
  public void testPublishWithDefaultInitiatingServer() throws InterruptedException {
    LatchListener.setLatch(new CountDownLatch(4));
    StringMPGame g1 = makeSimpleMPGame("1");
    StringMPGame g2 = makeSimpleMPGame("2");
    StringPlayer p1 = PONE;
    StringPlayer p2 = PTWO;

    publisher.publish(g1, p2);
    publisher.publish(g2, p1);
    LatchListener.getLatch().await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    LinkedHashMap<MultiPlayerGame, Player<String>> map = new LinkedHashMap<>();
    map.put(g1, p2);
    map.put(g2, p1);
    assertEquals(map, listener1.gamesPublished);
    LinkedHashMap<MultiPlayerGame, Player<String>> map1 = new LinkedHashMap<>();
    map1.put(g1, p2);
    map1.put(g2, p1);
    assertEquals(map1, listener2.gamesPublished);
    assertEquals(Arrays.asList(true, true), listener1.gamesInitiating);
    assertEquals(Arrays.asList(true, true), listener2.gamesInitiating);
  }

  @Test
  public void testPublishWithTrueInitiatingServer() throws InterruptedException {
    LatchListener.setLatch(new CountDownLatch(4));
    StringMPGame g1 = makeSimpleMPGame("1");
    StringMPGame g2 = makeSimpleMPGame("2");
    StringPlayer p1 = PONE;
    StringPlayer p2 = PTWO;

    publisher.publish(g1, p2, true);
    publisher.publish(g2, p1, true);
    LatchListener.getLatch().await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    LinkedHashMap<MultiPlayerGame, Player<String>> map = new LinkedHashMap<>();
    map.put(g1, p2);
    map.put(g2, p1);
    assertEquals(map, listener1.gamesPublished);
    LinkedHashMap<MultiPlayerGame, Player<String>> map1 = new LinkedHashMap<>();
    map1.put(g1, p2);
    map1.put(g2, p1);
    assertEquals(map1, listener2.gamesPublished);
    assertEquals(Arrays.asList(true, true), listener1.gamesInitiating);
    assertEquals(Arrays.asList(true, true), listener2.gamesInitiating);
  }

  @Test
  public void testPublishWithFalseInitiatingServer() throws InterruptedException {
    LatchListener.setLatch(new CountDownLatch(4));
    StringMPGame g1 = makeSimpleMPGame("1");
    StringMPGame g2 = makeSimpleMPGame("2");
    StringPlayer p1 = PONE;
    StringPlayer p2 = PTWO;

    publisher.publish(g1, p2, false);
    publisher.publish(g2, p1, false);
    LatchListener.getLatch().await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    LinkedHashMap<MultiPlayerGame, Player<String>> map = new LinkedHashMap<>(
        2);
    map.put(g1, p2);
    map.put(g2, p1);
    assertEquals(map, listener1.gamesPublished);
    LinkedHashMap<MultiPlayerGame, Player<String>> map1 = new LinkedHashMap<>(
        2);
    map1.put(g1, p2);
    map1.put(g2, p1);
    assertEquals(map1, listener2.gamesPublished);
    assertEquals(Arrays.asList(false, false), listener1.gamesInitiating);
    assertEquals(Arrays.asList(false, false), listener2.gamesInitiating);
  }

  @Test
  public void testPublishWithException() throws InterruptedException {
    GameListener mockListener = Mockito.mock(GameListener.class);
    Mockito.doThrow(new RuntimeException()).when(mockListener)
        .gameChanged(Matchers.isA(Game.class), Matchers.isA(Player.class), Matchers.eq(false));
    LatchListener.setLatch(new CountDownLatch(4));
    publisher = new GamePublisherImpl(10,
        new ArrayList<>(Arrays.asList(listener1, mockListener, listener2)));
    StringMPGame g1 = makeSimpleMPGame("1");
    StringMPGame g2 = makeSimpleMPGame("2");
    StringPlayer p1 = PONE;
    StringPlayer p2 = PTWO;

    publisher.publish(g1, p2, false);
    publisher.publish(g2, p1, false);
    LatchListener.getLatch().await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    LinkedHashMap<MultiPlayerGame, Player<String>> map = new LinkedHashMap<>(
        2);
    map.put(g1, p2);
    map.put(g2, p1);
    assertEquals(map, listener1.gamesPublished);
    LinkedHashMap<MultiPlayerGame, Player<String>> map1 = new LinkedHashMap<>(
        2);
    map1.put(g1, p2);
    map1.put(g2, p1);
    assertEquals(map1, listener2.gamesPublished);
    assertEquals(Arrays.asList(false, false),
        listener1.gamesInitiating);
    assertEquals(Arrays.asList(false, false),
        listener2.gamesInitiating);
    Mockito.verify(mockListener).gameChanged(g1, p2, false);
    Mockito.verify(mockListener).gameChanged(g2, p1, false);
  }

  private static class LatchListener implements GameListener<StringMPGame, StringPlayer> {

    private static CountDownLatch latch;
    private Map<Game, Player> gamesPublished = new HashMap<>();
    private List<Boolean> gamesInitiating = new LinkedList<>();

    static CountDownLatch getLatch() {
      return latch;
    }

    static void setLatch(CountDownLatch latch) {
      LatchListener.latch = latch;
    }

    @Override
    public void gameChanged(StringMPGame game, StringPlayer initiatingPlayer,
        boolean initiatingServer) {
      synchronized (this) {
        gamesPublished.put(game, initiatingPlayer);
        gamesInitiating.add(initiatingServer);
        LatchListener.latch.countDown();
      }

    }
  }
}
