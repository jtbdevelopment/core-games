package com.jtbdevelopment.games.publish.cluster;

import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage.ClusterMessageType;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.stringimpl.StringToStringConverter;
import java.io.Serializable;
import java.util.Optional;
import org.junit.Test;

/**
 * Date: 2/21/15 Time: 6:24 PM
 */
public class AbstractUpdatesFromClusterListenerTest {

  private PlayerPublisher playerPublisher = mock(PlayerPublisher.class);
  private AbstractGameRepository gameRepository = mock(AbstractGameRepository.class);
  private GamePublisher gamePublisher = mock(GamePublisher.class);
  private AbstractPlayerRepository playerRepository = mock(AbstractPlayerRepository.class);
  private TestListener listener = new TestListener(gamePublisher, playerPublisher,
      new StringToStringConverter(), playerRepository, gameRepository);


  private static class TestListener extends AbstractUpdatesFromClusterListener {

    TestListener(GamePublisher gamePublisher,
        PlayerPublisher playerPublisher,
        StringToIDConverter<? extends Serializable> stringToIDConverter,
        AbstractPlayerRepository playerRepository,
        AbstractGameRepository gameRepository) {
      super(gamePublisher, playerPublisher, stringToIDConverter, playerRepository, gameRepository);
    }
  }

  ;

  @Test
  public void testReceivePublishAllPlayers() {
    ClusterMessage message = new ClusterMessage();
    message.setClusterMessageType(ClusterMessageType.AllPlayersUpdate);
    listener.receiveClusterMessage(message);
    verify(playerPublisher).publishAll(false);
  }

  @Test
  public void testReceivePublishPlayer() {
    when(playerRepository.findById(GameCoreTestCase.reverse(PTWO.getIdAsString())))
        .thenReturn(Optional.of(PTWO));
    ClusterMessage message = new ClusterMessage();
    message.setClusterMessageType(ClusterMessageType.PlayerUpdate);
    message.setPlayerId(PTWO.getIdAsString());
    listener.receiveClusterMessage(message);
    verify(playerPublisher).publish(PTWO, false);
  }

  @Test
  public void testReceivePublishGame() {
    String gameId = "GID";
    MultiPlayerGame game = mock(MultiPlayerGame.class);
    when(playerRepository.findById(GameCoreTestCase.reverse(PTHREE.getIdAsString())))
        .thenReturn(Optional.of(PTHREE));
    when(gameRepository.findById(GameCoreTestCase.reverse(gameId)))
        .thenReturn(Optional.of(game));
    ClusterMessage message = new ClusterMessage();
    message.setClusterMessageType(ClusterMessageType.GameUpdate);
    message.setPlayerId(PTHREE.getIdAsString());
    message.setGameId(gameId);
    listener.receiveClusterMessage(message);
    verify(gamePublisher).publish(game, PTHREE, false);
  }

  @Test
  public void testReceivePublishGameNullPlayer() {
    String gameId = "GID";
    MultiPlayerGame game = mock(MultiPlayerGame.class);
    when(playerRepository.findById(null)).thenReturn(Optional.empty());
    when(gameRepository.findById(GameCoreTestCase.reverse(gameId)))
        .thenReturn(Optional.of(game));
    ClusterMessage message = new ClusterMessage();
    message.setClusterMessageType(ClusterMessageType.GameUpdate);
    message.setPlayerId(null);
    message.setGameId(gameId);
    listener.receiveClusterMessage(message);
    verify(gamePublisher).publish(game, null, false);
  }

}
