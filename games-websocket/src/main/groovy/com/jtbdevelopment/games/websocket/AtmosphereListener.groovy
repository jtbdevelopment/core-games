package com.jtbdevelopment.games.websocket

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GameListener
import com.jtbdevelopment.games.publish.PlayerListener
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.masking.MultiPlayerGameMasker
import groovy.transform.CompileStatic
import org.atmosphere.cpr.Broadcaster
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.Instant
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Date: 12/8/14
 * Time: 7:56 PM
 *
 * TODO - some tests for retry logic
 */
@Component
@CompileStatic
class AtmosphereListener implements GameListener, PlayerListener {
    private static final Logger logger = LoggerFactory.getLogger(AtmosphereListener.class)

    @Value('${atmosphere.LRU.seconds:300}')
    int retryRetentionPeriod = 300

    @Value('${atmosphere.threads:10}')
    int threads = 10

    @Value('${atmosphere.retries:5}')
    int retries = 5

    @Value('${atmosphere.retryPause:100}')
    int retryPause = 100

    protected ExecutorService service;

    private Map<Player, Instant> recentPublishes = new LinkedHashMap<Player, Instant>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Player, Instant> eldest) {
            return Instant.now().compareTo(eldest.value.plusSeconds(retryRetentionPeriod)) > 0
        }
    }

    private static abstract class PlayerCallable implements Callable<Boolean> {
        int attempts
        Player player
    }

    @Autowired(required = false)
    List<WebSocketPublicationListener> publicationListeners

    @Autowired
    MultiPlayerGameMasker gameMasker

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    @Autowired
    AtmosphereBroadcasterFactory broadcasterFactory

    @PostConstruct
    public void setUp() {
        service = Executors.newFixedThreadPool(threads)
    }

    @Override
    void allPlayersChanged(final boolean initiatingServer) {
        if (broadcasterFactory.broadcasterFactory) {
            logger.trace("Publishing all players changed.")
            try {
                Collection<Broadcaster> broadcasters = broadcasterFactory.broadcasterFactory.lookupAll()
                logger.trace("Publishing all players changed to " + broadcasters.size() + " players.")
                broadcasters.each {
                    Broadcaster broadcaster ->
                        try {
                            logger.trace("Looking up player for feed " + broadcaster.ID)
                            Player p = (Player) playerRepository.findOne(stringToIDConverter.convert(broadcaster.ID.replace('/livefeed/', '')))
                            if (p) {
                                logger.trace("Publishing all player changed to " + p.id)
                                broadcaster.broadcast(new WebSocketMessage(messageType: WebSocketMessage.MessageType.Player, player: p))
                            }
                        } catch (Exception e) {
                            logger.error("Failed to notify broadcaster for all player changed " + broadcaster.ID, e)
                        }
                }
            } catch (Exception e) {
                logger.error("Failed to lookup broadcasters.", e)
            }
        } else {
            logger.warn("No broadcaster in all players changed")
        }
    }

    @Override
    void playerChanged(final Player player, final boolean initiatingServer) {
        if (broadcasterFactory.broadcasterFactory) {
            publishWithRetry(new PlayerCallable() {
                {
                    this.player = player
                }

                @Override
                Boolean call() throws Exception {
                    return publishPlayerUpdate(player)
                }
            })

        } else {
            logger.warn("No broadcaster in player changed")
        }
    }

    private boolean publishPlayerUpdate(final Player player) {
        boolean status = false
        try {
            logger.trace("Publishing player changed to " + player.id)
            Broadcaster broadcaster = broadcasterFactory.broadcasterFactory.lookup(LiveFeedService.PATH_ROOT + player.idAsString)
            if (broadcaster != null) {
                broadcaster.broadcast(
                        new WebSocketMessage(
                                messageType: WebSocketMessage.MessageType.Player,
                                player: player
                        )
                )
                status = true
            } else {
                logger.trace("Player is not connected to this server for player changed " + player.id)
            }
        } catch (Exception e) {
            logger.error("Error publishing player update " + player.id, e);
        }
        publicationListeners?.each {
            try {
                it.publishedPlayerUpdate(player, status)
            } catch (Exception e) {
                logger.error("Error publishing to publication listener", e)
            }
        }
        return status
    }

    @Override
    void gameChanged(final MultiPlayerGame game, final Player initiatingPlayer, final boolean initiatingServer) {
        if (broadcasterFactory.broadcasterFactory) {
            try {
                Collection<Player> players = game.players.findAll {
                    Player p ->
                        initiatingPlayer == null || p != initiatingPlayer
                }

                logger.trace("Publishing " + game.id + " to " + players.size() + " players.")
                players.each {
                    Player player ->
                        publishWithRetry(new PlayerCallable() {
                            {
                                this.player = player
                            }

                            @Override
                            Boolean call() throws Exception {
                                return publishGameToPlayer(player, game)
                            }
                        })
                }
            } catch (Exception e) {
                logger.error("Error publishing game " + game.id, e);
            }
        } else {
            logger.warn("No broadcaster in game changed")
        }
    }

    private boolean publishGameToPlayer(final Player player, final MultiPlayerGame game) {
        logger.trace("Publishing game update on game " + game.id + " to player " + player.id)
        boolean status = false
        try {
            Broadcaster broadcaster = broadcasterFactory.broadcasterFactory.lookup(LiveFeedService.PATH_ROOT + player.idAsString)
            if (broadcaster) {
                broadcaster.broadcast(
                        new WebSocketMessage(
                                messageType: WebSocketMessage.MessageType.Game,
                                game: gameMasker.maskGameForPlayer((MultiPlayerGame) game, player)
                        )
                )

                status = true
            } else {
                logger.trace("Player " + player.id + " is not connected to this server for " + game.id + ".")
            }
        } catch (Exception e) {
            logger.error("Error publishing game " + game.id + " to player " + player.id, e);
        }
        publicationListeners?.each {
            try {
                it.publishedGameUpdateToPlayer(player, game, status)
            } catch (Exception e) {
                logger.error("Error publishing to publication listener", e)
            }
        }
        return status
    }

    private void publishWithRetry(final PlayerCallable playerCallable) {
        service.submit(new Runnable() {
            @Override
            void run() {
                if (playerCallable.attempts > 0) {
                    Thread.sleep(retryPause)
                }

                playerCallable.attempts++
                if (playerCallable.call()) {
                    recentPublishes[playerCallable.player] = Instant.now()
                    if (playerCallable.attempts > 1) {
                        logger.trace("Published to " + playerCallable.player.id + " in " + playerCallable.attempts + " attempts.")
                    }
                } else {
                    if (recentPublishes.containsKey(playerCallable.player)) {
                        if (playerCallable.attempts < retries) {
                            service.submit(this)
                        } else {
                            logger.trace("Failed to publish to " + playerCallable.player.id + " in " + playerCallable.attempts + " attempts.")
                        }
                    }
                    logger.trace("Not publishing to " + playerCallable.player.id + ", not in recent publishes");
                }
            }
        })
    }
}
