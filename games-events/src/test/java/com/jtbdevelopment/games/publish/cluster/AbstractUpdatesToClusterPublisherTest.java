package com.jtbdevelopment.games.publish.cluster;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage.ClusterMessageType;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 2/24/15 Time: 12:02 PM
 */
public class AbstractUpdatesToClusterPublisherTest {

  private String playerId = "TTTT";
  private String gameId = "#4521x";
  private AbstractMultiPlayerGame game = Mockito.mock(AbstractMultiPlayerGame.class);
  private AbstractPlayer player = Mockito.mock(AbstractPlayer.class);
  private ClusterPublisher publisher = new ClusterPublisher();

  @Before
  public void setup() {
    Mockito.when(game.getIdAsString()).thenReturn(gameId);
    Mockito.when(player.getIdAsString()).thenReturn(playerId);
  }

  @Test
  public void testPublishGameFromThisServer() {
    publisher.gameChanged(game, player, true);
    Assert.assertEquals(gameId, publisher.getClusterMessage().getGameId());
    Assert.assertEquals(playerId, publisher.getClusterMessage().getPlayerId());
    Assert.assertEquals(ClusterMessageType.GameUpdate,
        publisher.getClusterMessage().getClusterMessageType());
  }

  @Test
  public void testPublishGameFromThisServerWithNullPlayer() {
    publisher.gameChanged(game, null, true);
    Assert.assertEquals(gameId, publisher.getClusterMessage().getGameId());
    Assert.assertNull(publisher.getClusterMessage().getPlayerId());
    Assert.assertEquals(ClusterMessageType.GameUpdate,
        publisher.getClusterMessage().getClusterMessageType());
  }

  @Test
  public void testPublishGameNotFromThisServer() {
    publisher.gameChanged(game, player, false);
    Assert.assertNull(publisher.getClusterMessage());
  }

  @Test
  public void testPublishPlayerFromThisServer() {
    publisher.playerChanged(player, true);
    Assert.assertNull(publisher.getClusterMessage().getGameId());
    Assert.assertEquals(playerId, publisher.getClusterMessage().getPlayerId());
    Assert.assertEquals(ClusterMessageType.PlayerUpdate,
        publisher.getClusterMessage().getClusterMessageType());
  }

  @Test
  public void testPublishPlayerNotFromThisServer() {
    publisher.playerChanged(player, false);
    Assert.assertNull(publisher.getClusterMessage());
  }

  @Test
  public void testPublishAllPlayersFromThisServer() {
    publisher.allPlayersChanged(true);
    Assert.assertNull(publisher.getClusterMessage().getGameId());
    Assert.assertNull(publisher.getClusterMessage().getPlayerId());
    Assert.assertEquals(ClusterMessageType.AllPlayersUpdate,
        publisher.getClusterMessage().getClusterMessageType());
  }

  @Test
  public void testPublishAllPlayersNotFromThisServer() {
    publisher.allPlayersChanged(false);
    Assert.assertNull(publisher.getClusterMessage());
  }

  private static class ClusterPublisher extends AbstractUpdatesToClusterPublisher {

    private ClusterMessage clusterMessage = null;

    @Override
    protected void internalPublish(final ClusterMessage clusterMessage) {
      this.clusterMessage = clusterMessage;
    }

    ClusterMessage getClusterMessage() {
      return clusterMessage;
    }
  }
}
