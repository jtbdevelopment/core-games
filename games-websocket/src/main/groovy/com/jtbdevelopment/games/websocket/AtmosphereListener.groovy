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
import org.springframework.stereotype.Component

/**
 * Date: 12/8/14
 * Time: 7:56 PM
 */
@Component
@CompileStatic
class AtmosphereListener implements GameListener, PlayerListener {
    private static final Logger logger = LoggerFactory.getLogger(AtmosphereListener.class)

    private static PublishItem
    @Autowired
    MultiPlayerGameMasker gameMasker

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    @Autowired
    AtmosphereBroadcasterFactory broadcasterFactory

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
            publishPlayerUpdate(player)
        } else {
            logger.warn("No broadcaster in player changed")
        }
    }

    private boolean publishPlayerUpdate(final Player player) {
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
                return true
            } else {
                logger.trace("Player is not connected to this server for player changed " + player.id)
            }
        } catch (Exception e) {
            logger.error("Error publishing player update " + player.id, e);
        }
        return false
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
                    Player publish ->
                        publishGameToPlayer(publish, game);
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
        try {
            Broadcaster broadcaster = broadcasterFactory.broadcasterFactory.lookup(LiveFeedService.PATH_ROOT + player.idAsString)
            if (broadcaster) {
                broadcaster.broadcast(
                        new WebSocketMessage(
                                messageType: WebSocketMessage.MessageType.Game,
                                game: gameMasker.maskGameForPlayer((MultiPlayerGame) game, player)
                        )
                )
                return true
            } else {
                logger.trace("Player " + player.id + " is not connected to this server for " + game.id + ".")
            }
        } catch (Exception e) {
            logger.error("Error publishing game " + game.id + " to player " + player.id, e);
        }
        return false
    }
}
