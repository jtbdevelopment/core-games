package com.jtbdevelopment.games.publish.cluster;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.publish.GameListener;
import com.jtbdevelopment.games.publish.PlayerListener;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage.ClusterMessageType;
import com.jtbdevelopment.games.state.Game;

/**
 * Date: 2/17/15 Time: 7:10 AM
 */
public abstract class AbstractUpdatesToClusterPublisher
    implements GameListener<Game>, PlayerListener {

  protected abstract void internalPublish(final ClusterMessage clusterMessage);

  @Override
  public void gameChanged(final Game game, final Player initiatingPlayer,
      final boolean initiatingServer) {
    if (initiatingServer) {
      ClusterMessage message = new ClusterMessage();
      message.setGameId(game.getIdAsString());
      message.setClusterMessageType(ClusterMessageType.GameUpdate);
      if (initiatingPlayer != null) {
        message.setPlayerId(initiatingPlayer.getIdAsString());
      }
      internalPublish(message);
    }

  }

  @Override
  public void playerChanged(final Player player, final boolean initiatingServer) {
    if (initiatingServer) {
      ClusterMessage message = new ClusterMessage();
      message.setPlayerId(player.getIdAsString());
      message.setClusterMessageType(ClusterMessageType.PlayerUpdate);
      internalPublish(message);
    }

  }

  @Override
  public void allPlayersChanged(final boolean initiatingServer) {
    if (initiatingServer) {
      ClusterMessage message = new ClusterMessage();
      message.setClusterMessageType(ClusterMessageType.AllPlayersUpdate);
      internalPublish(message);
    }

  }

}
