package com.jtbdevelopment.games.websocket;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    listener.setRetries(2);
    listener.setRetryPause(1);
    listener.setUp();
    listener.publicationListeners = new ArrayList<>();
    when(b2.getID()).thenReturn(LiveFeedService.PATH_ROOT + PTWO.getIdAsString());
    when(b4.getID()).thenReturn(LiveFeedService.PATH_ROOT + PFOUR.getIdAsString());
    when(factory.lookup(LiveFeedService.PATH_ROOT + PTWO.getIdAsString())).thenReturn(b2);
    when(factory.lookup(LiveFeedService.PATH_ROOT + PFOUR.getIdAsString())).thenReturn(b4);
    when(factory.lookup(LiveFeedService.PATH_ROOT + PONE.getIdAsString())).thenReturn(null);
    when(factory.lookup(LiveFeedService.PATH_ROOT + PTHREE.getIdAsString()))
        .thenReturn(null);
    when(factoryFactory.getBroadcasterFactory()).thenReturn(factory);
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
    verify(publicationListener).publishedPlayerUpdate(PTWO, true);
    verify(publicationListener).publishedPlayerUpdate(PFOUR, true);
    verify(publicationListener).publishedPlayerUpdate(PONE, false);
    verify(publicationListener).publishedPlayerUpdate(PTHREE, false);
    verify(b2).broadcast(new WebSocketMessage(MessageType.Player, null, PTWO, null));
    verify(b4).broadcast(new WebSocketMessage(MessageType.Player, null, PFOUR, null));
  }

  @Test
  public void testPublishRefreshPlayerToAllValidConnectedPlayers() throws InterruptedException {
    Broadcaster junk = Mockito.mock(Broadcaster.class);
    when(junk.getID()).thenReturn(LiveFeedService.PATH_ROOT + "junk");
    when(factory.lookupAll())
        .thenReturn(new ArrayList<>(Arrays.asList(b2, b4, junk)));

    when(playerRepository.findById(GameCoreTestCase.reverse(PTWO.getIdAsString())))
        .thenReturn(Optional.of(PTWO));
    when(playerRepository.findById(GameCoreTestCase.reverse(PFOUR.getIdAsString())))
        .thenReturn(Optional.of(PFOUR));
    when(playerRepository.findById(GameCoreTestCase.reverse("JUNK")))
        .thenReturn(Optional.empty());

    listener.allPlayersChanged(initiatingServer);
    Thread.sleep(1);
    listener.service.shutdown();
    listener.service.awaitTermination(100, TimeUnit.SECONDS);
    verify(b2).broadcast(new WebSocketMessage(MessageType.Player, null, PTWO, null));
    verify(b4).broadcast(new WebSocketMessage(MessageType.Player, null, PFOUR, null));
    verify(junk, Mockito.never()).broadcast(Matchers.any());
    verify(publicationListener, Mockito.never()).publishedPlayerUpdate(PTWO, true);
    verify(publicationListener, Mockito.never()).publishedPlayerUpdate(PFOUR, true);
  }

  @Test
  public void testPublishGameToConnectedNonInitiatingPlayers() throws InterruptedException {
    MultiPlayerGame game = Mockito.mock(MultiPlayerGame.class);
    when(game.getId()).thenReturn("An ID!");
    when(game.getAllPlayers())
        .thenReturn(new ArrayList<>(Arrays.asList(PONE, PTWO, PTHREE, PFOUR)));
    when(game.getPlayers())
        .thenReturn(new ArrayList<>(Arrays.asList(PONE, PTWO, PTHREE, PFOUR)));
    MaskedMultiPlayerGame mg2 = Mockito.mock(MaskedMultiPlayerGame.class);
    when(mg2.getId()).thenReturn("mg2");
    MaskedMultiPlayerGame mg4 = Mockito.mock(MaskedMultiPlayerGame.class);
    when(mg4.getId()).thenReturn("mg4");
    GameMasker gameMasker = Mockito.mock(GameMasker.class);
    when(gameMasker.maskGameForPlayer(game, PTWO)).thenReturn(mg2);
    when(gameMasker.maskGameForPlayer(game, PFOUR)).thenReturn(mg4);
    listener.gameMasker = gameMasker;

    listener.gameChanged(game, PONE, initiatingServer);
    listener.service.shutdown();
    listener.service.awaitTermination(100, TimeUnit.SECONDS);
    verify(factory, Mockito.never())
        .lookup(LiveFeedService.PATH_ROOT + PONE.getIdAsString());
    verify(publicationListener).publishedGameUpdateToPlayer(PTWO, game, true);
    verify(publicationListener).publishedGameUpdateToPlayer(PFOUR, game, true);
    verify(publicationListener, Mockito.never())
        .publishedGameUpdateToPlayer(PONE, game, false);
    verify(publicationListener, Mockito.never())
        .publishedGameUpdateToPlayer(PONE, game, true);
    verify(publicationListener).publishedGameUpdateToPlayer(PTHREE, game, false);
    verify(b2).broadcast(new WebSocketMessage(MessageType.Game, mg2, null, null));
    verify(b4).broadcast(new WebSocketMessage(MessageType.Game, mg4, null, null));
  }
}
