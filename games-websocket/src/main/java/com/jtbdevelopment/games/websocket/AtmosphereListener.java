package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.publish.GameListener;
import com.jtbdevelopment.games.publish.PlayerListener;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.websocket.WebSocketMessage.MessageType;
import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.atmosphere.cpr.Broadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 12/8/14 Time: 7:56 PM
 *
 * TODO - some tests for retry logic TODO - split into game and player?, smells a bit
 */
@Component
public class AtmosphereListener<ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>,
    M extends AbstractMaskedGame<FEATURES>>
    implements GameListener<IMPL, P>, PlayerListener<ID, P> {

  private static final Logger logger = LoggerFactory.getLogger(AtmosphereListener.class);
  private final List<WebSocketPublicationListener<ID, Instant, FEATURES, IMPL, P>> publicationListeners;
  private final GameMasker<ID, IMPL, M> gameMasker;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final StringToIDConverter<ID> stringToIDConverter;
  private final AtmosphereBroadcasterFactory broadcasterFactory;
  private final Map<Player, Instant> recentPublishes;
  private final int retries;
  private final int retryPause;
  ExecutorService service;

  //  keep listeners generic for spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  public AtmosphereListener(
      @Autowired(required = false) final List<WebSocketPublicationListener> publicationListeners,
      final GameMasker<ID, IMPL, M> gameMasker,
      final AbstractPlayerRepository<ID, P> playerRepository,
      final StringToIDConverter<ID> stringToIDConverter,
      final AtmosphereBroadcasterFactory broadcasterFactory,
      @Value("${atmosphere.LRU.seconds:300}") final int retryRetentionPeriod,
      @Value("${atmosphere.threads:10}") final int threads,
      @Value("${atmosphere.retries:5}") final int retries,
      @Value("${atmosphere.retryPause:500}") final int retryPause) {
    this.publicationListeners = new LinkedList<>();
    if (publicationListeners != null) {
      //noinspection unchecked
      publicationListeners.forEach(AtmosphereListener.this.publicationListeners::add);
    }
    this.gameMasker = gameMasker;
    this.playerRepository = playerRepository;
    this.stringToIDConverter = stringToIDConverter;
    this.broadcasterFactory = broadcasterFactory;
    this.retries = retries;
    this.retryPause = retryPause;
    service = Executors.newFixedThreadPool(threads);
    recentPublishes = new LinkedHashMap<Player, Instant>() {
      @Override
      protected boolean removeEldestEntry(final Entry<Player, Instant> eldest) {
        return Instant.now().compareTo(eldest.getValue().plusSeconds(retryRetentionPeriod)) > 0;
      }
    };

  }

  @Override
  public void allPlayersChanged(final boolean initiatingServer) {
    if (broadcasterFactory.getBroadcasterFactory() != null) {
      logger.trace("Publishing all players changed.");
      try {
        List<Broadcaster> broadcasters = new LinkedList<>(broadcasterFactory.getBroadcasterFactory()
            .lookupAll());
        logger.trace("Publishing all players changed to " + broadcasters.size() + " players.");
        broadcasters.forEach(broadcaster -> {
          try {
            logger.trace("Looking up player for feed " + broadcaster.getID());
            String playerId = broadcaster.getID().replace(LiveFeedService.PATH_ROOT, "");
            //noinspection ConstantConditions
            Optional<P> optional = playerRepository.findById(stringToIDConverter.convert(playerId));
            if (optional.isPresent()) {
              logger
                  .trace("Publishing all player changed to " + optional.get().getId());
              WebSocketMessage message = new WebSocketMessage();
              message.setMessageType(MessageType.Player);
              message.setPlayer(optional.get());
              broadcaster.broadcast(message);
            }

          } catch (Exception e) {
            logger.error(
                "Failed to notify broadcaster for all player changed " + broadcaster.getID(), e);
          }

        });
      } catch (Exception e) {
        logger.error("Failed to lookup broadcasters.", e);
      }

    } else {
      logger.warn("No broadcaster in all players changed");
    }

  }

  @Override
  public void playerChanged(final P player, final boolean initiatingServer) {
    if (broadcasterFactory.getBroadcasterFactory() != null) {
      publishWithRetry(new PlayerCallable() {
        @Override
        public Boolean call() throws Exception {
          return publishPlayerUpdate(player);
        }

      });

    } else {
      logger.warn("No broadcaster in player changed");
    }

  }

  private boolean publishPlayerUpdate(final P player) {
    final boolean[] status = new boolean[]{false};
    try {
      logger.trace("Publishing player changed to " + player.getId());
      Broadcaster broadcaster = broadcasterFactory.getBroadcasterFactory()
          .lookup(LiveFeedService.PATH_ROOT + player.getIdAsString());
      if (broadcaster != null) {
        WebSocketMessage message = new WebSocketMessage();
        message.setMessageType(MessageType.Player);
        message.setPlayer(player);
        broadcaster.broadcast(message);
        status[0] = true;
      } else {
        logger.trace("Player is not connected to this server for player changed " + player.getId());
      }

    } catch (Exception e) {
      logger.error("Error publishing player update " + player.getId(), e);
    }

    publicationListeners.forEach(listner -> {
      try {
        logger.info("trace {} {}", player.getIdAsString(), status[0]);
        listner.publishedPlayerUpdate(player, status[0]);
      } catch (Exception e) {
        logger.error("Error publishing to publication listener", e);
      }

    });
    return status[0];
  }

  @Override
  public void gameChanged(final IMPL game, final P initiatingPlayer,
      final boolean initiatingServer) {
    if (broadcasterFactory.getBroadcasterFactory() != null) {
      try {
        //noinspection unchecked
        List<P> players = game.getAllPlayers().stream()
            .map(p -> (P) p)
            .filter(x -> initiatingPlayer == null || !x.equals(initiatingPlayer))
            .collect(Collectors.toList());

        logger.trace(
            "Publishing {} to {} players.",
            game.getIdAsString(),
            players.size());
        players.forEach(player -> publishWithRetry(new PlayerCallable() {
          @Override
          public Boolean call() throws Exception {
            return publishGameToPlayer(player, game);
          }
        }));
      } catch (Exception e) {
        logger.error("Error publishing game " + game.getId(), e);
      }

    } else {
      logger.warn("No broadcaster in game changed");
    }

  }

  private boolean publishGameToPlayer(final P player, final IMPL game) {
    logger.trace("Publishing game update on game " + game.getId() + " to player " + player.getId());
    final boolean[] status = new boolean[]{false};
    try {
      Broadcaster broadcaster = broadcasterFactory.getBroadcasterFactory()
          .lookup(LiveFeedService.PATH_ROOT + player.getIdAsString());
      if (broadcaster != null) {
        WebSocketMessage message = new WebSocketMessage();
        message.setMessageType(MessageType.Game);
        message.setGame(gameMasker.maskGameForPlayer(game, player));
        broadcaster.broadcast(message);

        status[0] = true;
      } else {
        logger.trace(
            "Player {}  is not connected to this server for {}.",
            player.getIdAsString(),
            game.getIdAsString());
      }

    } catch (Exception e) {
      logger.error("Error publishing game " + game.getId() + " to player " + player.getId(), e);
    }

    publicationListeners.forEach(listener -> {
      try {
        listener.publishedGameUpdateToPlayer(player, game, status[0]);
      } catch (Exception e) {
        logger.error("Error publishing to publication listener", e);
      }
    });

    return status[0];
  }

  private void publishWithRetry(final PlayerCallable playerCallable) {
    service.submit(new Runnable() {
      @Override
      public void run() {
        try {
          if (playerCallable.getAttempts() > 0) {
            logger.trace("enter sleep for {}", playerCallable.getPlayer().getIdAsString());
            Thread.sleep(retryPause);
            logger.trace("exit sleep for {}", playerCallable.getPlayer().getIdAsString());
          }

          playerCallable.attempts += 1;
          if (playerCallable.call()) {
            recentPublishes.put(playerCallable.getPlayer(), Instant.now());
            if (playerCallable.getAttempts() > 1) {
              logger.trace(
                  "Published to {} in {} attempts.",
                  playerCallable.getPlayer().getId(),
                  playerCallable.attempts);
            }

          } else {
            if (recentPublishes.containsKey(playerCallable.getPlayer())) {
              if (playerCallable.getAttempts() < retries) {
                logger.trace("submit retry for {}", playerCallable.getPlayer().getIdAsString());
                service.submit(this);
              } else {
                logger.trace(
                    "Failed to publish to {} in {} attempts.",
                    playerCallable.getPlayer().getId(),
                    playerCallable.getAttempts());
              }

            }

            logger.trace("Not publishing to {}, not in recent publishes.",
                playerCallable.getPlayer().getId());
          }

        } catch (Exception e) {
          logger.warn("Failed to publish to " + playerCallable.getPlayer().getId(), e);
        }
      }

    });
  }

  private static abstract class PlayerCallable implements Callable<Boolean> {

    private int attempts;
    private Player player;

    int getAttempts() {
      return attempts;
    }

    Player getPlayer() {
      return player;
    }

    void setPlayer(Player player) {
      this.player = player;
    }
  }
}
