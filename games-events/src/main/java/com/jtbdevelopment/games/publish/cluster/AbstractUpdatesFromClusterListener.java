package com.jtbdevelopment.games.publish.cluster;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.state.AbstractGame;
import java.io.Serializable;
import java.util.Optional;

/**
 * Date: 2/17/15 Time: 6:56 AM
 */
public abstract class AbstractUpdatesFromClusterListener<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    P extends Player<ID>> {

  private final GamePublisher<IMPL, P> gamePublisher;
  private final PlayerPublisher playerPublisher;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final StringToIDConverter<ID> stringToIDConverter;
  private final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository;

  protected AbstractUpdatesFromClusterListener(
      final GamePublisher<IMPL, P> gamePublisher,
      final PlayerPublisher playerPublisher,
      final StringToIDConverter<ID> stringToIDConverter,
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository) {
    this.gamePublisher = gamePublisher;
    this.playerPublisher = playerPublisher;
    this.playerRepository = playerRepository;
    this.stringToIDConverter = stringToIDConverter;
    this.gameRepository = gameRepository;
  }

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

  private void receivePublishAllPlayers() {
    playerPublisher.publishAll(false);
  }

  private void receivePublishPlayer(final String id) {
    ID converted = stringToIDConverter.convert(id);
    if (converted != null) {
      Optional<? extends Player<ID>> optional = playerRepository
          .findById(converted);
      optional.ifPresent(player -> playerPublisher.publish(player, false));
    }
  }

  private void receivePublishGame(final String gameId, final String playerId) {
    ID convertedPlayerId = stringToIDConverter.convert(playerId);
    Optional<P> optionalPlayer = Optional.empty();
    if (convertedPlayerId != null) {
      optionalPlayer = playerRepository.findById(convertedPlayerId);
    }
    ID convertedGameId = stringToIDConverter.convert(gameId);
    Optional<IMPL> optionalGame = Optional.empty();
    if (convertedGameId != null) {
      optionalGame = gameRepository.findById(convertedGameId);
    }
    if (optionalGame.isPresent()) {
      gamePublisher.publish(
          optionalGame.get(),
          optionalPlayer.orElse(null),
          false);
    }
  }
}
