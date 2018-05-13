package com.jtbdevelopment.games.publish.cluster;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.publish.GameListener;
import com.jtbdevelopment.games.publish.PlayerListener;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage.ClusterMessageType;
import com.jtbdevelopment.games.state.AbstractGame;
import java.io.Serializable;

/**
 * Date: 2/17/15 Time: 7:10 AM
 */
public abstract class AbstractUpdatesToClusterPublisher<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>>
    implements GameListener<IMPL, P>, PlayerListener<ID, P> {

  protected abstract void internalPublish(final ClusterMessage clusterMessage);

  @Override
  public void gameChanged(
      final IMPL game,
      final P initiatingPlayer,
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
  public void playerChanged(final P player, final boolean initiatingServer) {
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
