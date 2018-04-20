package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.publish.GameListener;
import com.jtbdevelopment.games.publish.PlayerListener;
import com.jtbdevelopment.games.state.Game;
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
import javax.annotation.PostConstruct;
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
public class AtmosphereListener implements GameListener<Game>, PlayerListener {

  private static final Logger logger = LoggerFactory.getLogger(AtmosphereListener.class);
  protected ExecutorService service;
  @Autowired(required = false)
  protected List<WebSocketPublicationListener> publicationListeners;
  @Autowired
  protected GameMasker gameMasker;
  @Autowired
  protected AbstractPlayerRepository playerRepository;
  @Autowired
  protected StringToIDConverter stringToIDConverter;
  @Autowired
  protected AtmosphereBroadcasterFactory broadcasterFactory;
  @Value("${atmosphere.LRU.seconds:300}")
  private int retryRetentionPeriod = 300;
  @Value("${atmosphere.threads:10}")
  private int threads = 10;
  @Value("${atmosphere.retries:5}")
  private int retries = 5;
  @Value("${atmosphere.retryPause:500}")
  private int retryPause = 500;
  private Map<Player, Instant> recentPublishes = new LinkedHashMap<Player, Instant>() {
    @Override
    protected boolean removeEldestEntry(final Entry<Player, Instant> eldest) {
      return Instant.now().compareTo(eldest.getValue().plusSeconds(getRetryRetentionPeriod())) > 0;
    }

  };

  @PostConstruct
  public void setUp() {
    service = Executors.newFixedThreadPool(threads);
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
            Optional<? extends Player> optional = playerRepository.findById(
                (Serializable) stringToIDConverter
                    .convert(broadcaster.getID().replace("/livefeed/", "")));
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
  public void playerChanged(final Player player, final boolean initiatingServer) {
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

  private boolean publishPlayerUpdate(final Player player) {
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
        listner.publishedPlayerUpdate(player, status[0]);
      } catch (Exception e) {
        logger.error("Error publishing to publication listener", e);
      }

    });
    return status[0];
  }

  @Override
  public void gameChanged(final Game game, final Player initiatingPlayer,
      final boolean initiatingServer) {
    if (broadcasterFactory.getBroadcasterFactory() != null) {
      try {
        List<Player> players = (List<Player>) game.getAllPlayers().stream()
            .filter(x -> initiatingPlayer == null || !x.equals(initiatingPlayer)).collect(
                Collectors.toList());

        logger.trace(
            "Publishing " + game.getId() + " to " + players.size() + " players.");
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

  private boolean publishGameToPlayer(final Player player, final Game game) {
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
            "Player " + player.getId() + " is not connected to this server for " + game.getId()
                + ".");
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
            Thread.sleep(getRetryPause());
          }

          playerCallable.setAttempts(playerCallable.attempts++);
          if (playerCallable.call()) {
            recentPublishes.put(playerCallable.getPlayer(), Instant.now());
            if (playerCallable.getAttempts() > 1) {
              logger.trace(
                  "Published to " + playerCallable.getPlayer().getId() + " in "
                      + playerCallable
                      .getAttempts() + " attempts.");
            }

          } else {
            if (recentPublishes.containsKey(playerCallable.getPlayer())) {
              if (playerCallable.getAttempts() < getRetries()) {
                service.submit(this);
              } else {
                logger.trace(
                    "Failed to publish to " + playerCallable.getPlayer().getId()
                        + " in "
                        + playerCallable.getAttempts() + " attempts.");
              }

            }

            logger.trace("Not publishing to " + playerCallable.getPlayer().getId()
                + ", not in recent publishes");
          }

        } catch (Exception e) {
          logger.warn("Failed to publish to " + playerCallable.getPlayer().getId(), e);
        }
      }

    });
  }

  public int getRetryRetentionPeriod() {
    return retryRetentionPeriod;
  }

  public void setRetryRetentionPeriod(int retryRetentionPeriod) {
    this.retryRetentionPeriod = retryRetentionPeriod;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public int getRetries() {
    return retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public int getRetryPause() {
    return retryPause;
  }

  public void setRetryPause(int retryPause) {
    this.retryPause = retryPause;
  }

  private static abstract class PlayerCallable implements Callable<Boolean> {

    private int attempts;
    private Player player;

    public int getAttempts() {
      return attempts;
    }

    public void setAttempts(int attempts) {
      this.attempts = attempts;
    }

    public Player getPlayer() {
      return player;
    }

    public void setPlayer(Player player) {
      this.player = player;
    }
  }
}
