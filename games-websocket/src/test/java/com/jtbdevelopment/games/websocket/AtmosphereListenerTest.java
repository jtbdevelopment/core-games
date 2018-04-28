package com.jtbdevelopment.games.websocket;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame;
import com.jtbdevelopment.games.stringimpl.StringToStringConverter;
import com.jtbdevelopment.games.websocket.WebSocketMessage.MessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 12/22/14 Time: 7:18 PM
 */
public class AtmosphereListenerTest {

  private AtmosphereListener listener;
  private Broadcaster b2 = Mockito.mock(Broadcaster.class);
  private Broadcaster b4 = Mockito.mock(Broadcaster.class);
  private BroadcasterFactory factory = Mockito.mock(BroadcasterFactory.class);
  private AtmosphereBroadcasterFactory factoryFactory = Mockito
      .mock(AtmosphereBroadcasterFactory.class);
  private WebSocketPublicationListener publicationListener = Mockito
      .mock(WebSocketPublicationListener.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private boolean initiatingServer = new Random().nextBoolean();

  @Before
  public void setUp() throws Exception {
    listener = new AtmosphereListener();
    listener.stringToIDConverter = new StringToStringConverter();
    listener.setThreads(10);
    listener.setRetries(1);
    listener.setRetryPause(1);
    listener.setUp();
    listener.publicationListeners = new ArrayList<>();
    Mockito.when(b2.getID()).thenReturn(LiveFeedService.PATH_ROOT + PTWO.getIdAsString());
    Mockito.when(b4.getID()).thenReturn(LiveFeedService.PATH_ROOT + PFOUR.getIdAsString());
    Mockito.when(factory.lookup(LiveFeedService.PATH_ROOT + PTWO.getIdAsString())).thenReturn(b2);
    Mockito.when(factory.lookup(LiveFeedService.PATH_ROOT + PFOUR.getIdAsString())).thenReturn(b4);
    Mockito.when(factory.lookup(LiveFeedService.PATH_ROOT + PONE.getIdAsString())).thenReturn(null);
    Mockito.when(factory.lookup(LiveFeedService.PATH_ROOT + PTHREE.getIdAsString()))
        .thenReturn(null);
    Mockito.when(factoryFactory.getBroadcasterFactory()).thenReturn(factory);
    listener.broadcasterFactory = factoryFactory;
    listener.publicationListeners.add(publicationListener);
    listener.playerRepository = playerRepository;
  }

  @Test
  public void testPublishPlayerToConnectedPlayer() throws InterruptedException {
    Arrays.asList(PONE, PTWO, PTHREE, PFOUR)
        .forEach(player -> listener.playerChanged(player, initiatingServer));
    Thread.sleep(2000);
    listener.service.shutdown();
    listener.service.awaitTermination(100, TimeUnit.SECONDS);
    Mockito.verify(publicationListener).publishedPlayerUpdate(PTWO, true);
    Mockito.verify(publicationListener).publishedPlayerUpdate(PFOUR, true);
    Mockito.verify(publicationListener).publishedPlayerUpdate(PONE, false);
    Mockito.verify(publicationListener).publishedPlayerUpdate(PTHREE, false);
    Mockito.verify(b2).broadcast(new WebSocketMessage(MessageType.Player, null, PTWO, null));
    Mockito.verify(b4).broadcast(new WebSocketMessage(MessageType.Player, null, PFOUR, null));
  }

  @Test
  public void testPublishRefreshPlayerToAllValidConnectedPlayers() throws InterruptedException {
    Broadcaster junk = Mockito.mock(Broadcaster.class);
    Mockito.when(junk.getID()).thenReturn(LiveFeedService.PATH_ROOT + "junk");
    Mockito.when(factory.lookupAll())
        .thenReturn(new ArrayList<>(Arrays.asList(b2, b4, junk)));

    Mockito.when(playerRepository.findById(GameCoreTestCase.reverse(PTWO.getIdAsString())))
        .thenReturn(Optional.of(PTWO));
    Mockito.when(playerRepository.findById(GameCoreTestCase.reverse(PFOUR.getIdAsString())))
        .thenReturn(Optional.of(PFOUR));
    Mockito.when(playerRepository.findById(GameCoreTestCase.reverse("JUNK")))
        .thenReturn(Optional.empty());

    listener.allPlayersChanged(initiatingServer);
    Thread.sleep(1);
    listener.service.shutdown();
    listener.service.awaitTermination(100, TimeUnit.SECONDS);
    Mockito.verify(b2).broadcast(new WebSocketMessage(MessageType.Player, null, PTWO, null));
    Mockito.verify(b4).broadcast(new WebSocketMessage(MessageType.Player, null, PFOUR, null));
    Mockito.verify(junk, Mockito.never()).broadcast(Matchers.any());
    Mockito.verify(publicationListener, Mockito.never()).publishedPlayerUpdate(PTWO, true);
    Mockito.verify(publicationListener, Mockito.never()).publishedPlayerUpdate(PFOUR, true);
  }

  @Test
  public void testPublishGameToConnectedNonInitiatingPlayers() throws InterruptedException {
    MultiPlayerGame game = Mockito.mock(MultiPlayerGame.class);
    Mockito.when(game.getId()).thenReturn("An ID!");
    Mockito.when(game.getAllPlayers())
        .thenReturn(new ArrayList<>(Arrays.asList(PONE, PTWO, PTHREE, PFOUR)));
    Mockito.when(game.getPlayers())
        .thenReturn(new ArrayList<>(Arrays.asList(PONE, PTWO, PTHREE, PFOUR)));
    MaskedMultiPlayerGame mg2 = Mockito.mock(MaskedMultiPlayerGame.class);
    Mockito.when(mg2.getId()).thenReturn("mg2");
    MaskedMultiPlayerGame mg4 = Mockito.mock(MaskedMultiPlayerGame.class);
    Mockito.when(mg4.getId()).thenReturn("mg4");
    GameMasker gameMasker = Mockito.mock(GameMasker.class);
    Mockito.when(gameMasker.maskGameForPlayer(game, PTWO)).thenReturn(mg2);
    Mockito.when(gameMasker.maskGameForPlayer(game, PFOUR)).thenReturn(mg4);
    listener.gameMasker = gameMasker;

    listener.gameChanged(game, PONE, initiatingServer);
    listener.service.shutdown();
    listener.service.awaitTermination(100, TimeUnit.SECONDS);
    Mockito.verify(factory, Mockito.never())
        .lookup(LiveFeedService.PATH_ROOT + PONE.getIdAsString());
    Mockito.verify(publicationListener).publishedGameUpdateToPlayer(PTWO, game, true);
    Mockito.verify(publicationListener).publishedGameUpdateToPlayer(PFOUR, game, true);
    Mockito.verify(publicationListener, Mockito.never())
        .publishedGameUpdateToPlayer(PONE, game, false);
    Mockito.verify(publicationListener, Mockito.never())
        .publishedGameUpdateToPlayer(PONE, game, true);
    Mockito.verify(publicationListener).publishedGameUpdateToPlayer(PTHREE, game, false);
    Mockito.verify(b2).broadcast(new WebSocketMessage(MessageType.Game, mg2, null, null));
    Mockito.verify(b4).broadcast(new WebSocketMessage(MessageType.Game, mg4, null, null));
  }
}
