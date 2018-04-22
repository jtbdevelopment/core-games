package com.jtbdevelopment.games.publish;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertEquals;

import com.jtbdevelopment.games.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Date: 2/6/15 Time: 7:02 PM
 */
public class PlayerPublisherTest {

  private LatchListener listener1 = new LatchListener();
  private LatchListener listener2 = new LatchListener();
  private PlayerPublisher publisher = new PlayerPublisher(10, Arrays.asList(listener1, listener2));

  @Test
  @SuppressWarnings("unchecked")
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
    publisher = new PlayerPublisher(1, new ArrayList());
    publisher.publish(PONE);
    publisher.publish(PTWO);
    publisher.service.shutdown();

    //  Not crashing is success
  }

  @Test
  public void testPublishDefaultInitiatingServer() throws InterruptedException {
    LatchListener.latch = new CountDownLatch(4);
    publisher.publish(PTWO);
    publisher.publish(PONE);
    LatchListener.latch.await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    assertEquals(new HashSet<>(Arrays.asList(PTWO, PONE)),
        listener1.getPlayersPublished());
    assertEquals(new HashSet<>(Arrays.asList(PTWO, PONE)),
        listener2.getPlayersPublished());
    assertEquals(
        new HashSet<>(Arrays.asList(true, true)),
        listener1.getPlayersPublishedInitiating());
    assertEquals(
        new HashSet<>(Arrays.asList(true, true)),
        listener2.getPlayersPublishedInitiating());
  }

  @Test
  public void testPublishTrueInitiatingServer() throws InterruptedException {
    LatchListener.latch = new CountDownLatch(4);
    publisher.publish(PTWO, true);
    publisher.publish(PONE, true);
    LatchListener.latch.await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    assertEquals(new HashSet<>(Arrays.asList(PTWO, PONE)),
        listener1.getPlayersPublished());
    assertEquals(new HashSet<>(Arrays.asList(PTWO, PONE)),
        listener2.getPlayersPublished());
    assertEquals(new HashSet<>(Arrays.asList(true, true)),
        listener1.getPlayersPublishedInitiating());
    assertEquals(
        new HashSet<>(Arrays.asList(true, true)),
        listener2.getPlayersPublishedInitiating());
  }

  @Test
  public void testPublishFalseInitiatingServer() throws InterruptedException {
    LatchListener.latch = new CountDownLatch(4);
    publisher.publish(PTWO, false);
    publisher.publish(PONE, false);
    LatchListener.latch.await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    assertEquals(new HashSet<>(Arrays.asList(PTWO, PONE)),
        listener1.getPlayersPublished());
    assertEquals(new HashSet<>(Arrays.asList(PTWO, PONE)),
        listener2.getPlayersPublished());
    assertEquals(
        new HashSet<>(Arrays.asList(false, false)),
        listener1.getPlayersPublishedInitiating());
    assertEquals(
        new HashSet<>(Arrays.asList(false, false)),
        listener2.getPlayersPublishedInitiating());
  }

  @Test
  public void testPublishAllWithNoListeners() {
    publisher = new PlayerPublisher(10, new ArrayList<>());
    publisher.publishAll();
    publisher.service.shutdown();
    //  Not crashing is success
  }

  @Test
  public void testPublishAllDefaultInitiatingServer() throws InterruptedException {
    LatchListener.latch = new CountDownLatch(2);
    publisher.publishAll();
    LatchListener.latch.await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    assertEquals(
        new HashSet<>(Collections.singletonList(true)),
        listener1.getAllPublishedInitiating());
    assertEquals(
        new HashSet<>(Collections.singletonList(true)),
        listener2.getAllPublishedInitiating());
  }

  @Test
  public void testPublishAllTrueInitiatingServer() throws InterruptedException {
    LatchListener.latch = new CountDownLatch(2);
    publisher.publishAll(true);
    publisher.publishAll(true);
    LatchListener.latch.await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    assertEquals(
        new HashSet<>(Collections.singletonList(true)),
        listener1.getAllPublishedInitiating());
    assertEquals(
        new HashSet<>(Collections.singletonList(true)),
        listener2.getAllPublishedInitiating());
  }

  @Test
  public void testPublishAllFalseInitiatingServer() throws InterruptedException {
    LatchListener.latch = new CountDownLatch(2);
    publisher.publishAll(false);
    LatchListener.latch.await(1, TimeUnit.SECONDS);
    publisher.service.shutdown();
    assertEquals(
        new HashSet<>(Collections.singletonList(false)),
        listener1.getAllPublishedInitiating());
    assertEquals(
        new HashSet<>(Collections.singletonList(false)),
        listener2.getAllPublishedInitiating());
  }

  private static class LatchListener implements PlayerListener {

    static CountDownLatch latch;
    private Set<Player> playersPublished = new HashSet<>();
    private Set<Boolean> playersPublishedInitiating = new HashSet<>();
    private Set<Boolean> allPublishedInitiating = new HashSet<>();

    @Override
    public void playerChanged(Player player, boolean initiatingServer) {
      synchronized (this) {
        playersPublished.add(player);
        playersPublishedInitiating.add(initiatingServer);
        LatchListener.latch.countDown();
      }

    }

    @Override
    public void allPlayersChanged(boolean initiatingServer) {
      synchronized (this) {
        allPublishedInitiating.add(initiatingServer);
        LatchListener.latch.countDown();
      }

    }

    Set<Player> getPlayersPublished() {
      return playersPublished;
    }

    Set<Boolean> getPlayersPublishedInitiating() {
      return playersPublishedInitiating;
    }

    Set<Boolean> getAllPublishedInitiating() {
      return allPublishedInitiating;
    }
  }
}
