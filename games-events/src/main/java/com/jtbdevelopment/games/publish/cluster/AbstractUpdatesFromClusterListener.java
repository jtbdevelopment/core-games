package com.jtbdevelopment.games.publish.cluster;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.state.Game;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 2/17/15 Time: 6:56 AM
 */
public abstract class AbstractUpdatesFromClusterListener {

  @Autowired
  protected GamePublisher gamePublisher;
  @Autowired
  protected PlayerPublisher playerPublisher;
  @Autowired
  protected AbstractPlayerRepository playerRepository;
  @Autowired
  protected StringToIDConverter<? extends Serializable> stringToIDConverter;
  @Autowired
  protected AbstractGameRepository gameRepository;

  protected void receiveClusterMessage(final ClusterMessage clusterMessage) {
    switch (clusterMessage.getClusterMessageType()) {
      case GameUpdate:
        receivePublishGame(clusterMessage.getGameId(), clusterMessage.getPlayerId());
        break;
      case PlayerUpdate:
        receivePublishPlayer(clusterMessage.getPlayerId());
        break;
      case AllPlayersUpdate:
        receivePublishAllPlayers();
        break;
    }
  }

  protected void receivePublishAllPlayers() {
    playerPublisher.publishAll(false);
  }

  protected void receivePublishPlayer(final String id) {
    Optional<? extends Player> optional = playerRepository
        .findById(stringToIDConverter.convert(id));
    if (optional.isPresent()) {
      playerPublisher.publish(optional.get(), false);
    }

  }

  protected void receivePublishGame(final String gameId, final String playerId) {
    if (gameRepository != null) {
      Optional<? extends Player> optionalPlayer = playerRepository
          .findById(stringToIDConverter.convert(playerId));
      if (optionalPlayer.isPresent() || playerId == null) {
        Optional<? extends Game> optionalGame = gameRepository
            .findById(stringToIDConverter.convert(gameId));
        if (optionalGame.isPresent()) {
          gamePublisher.publish(
              optionalGame.get(),
              optionalPlayer.isPresent() ? optionalPlayer.get() : null,
              false);
        }

      }

    }

  }
}
