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
import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.Universe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 12/8/14
 * Time: 7:56 PM
 */
@Component
@CompileStatic
class AtmosphereListener implements GameListener, PlayerListener {
    @Autowired
    MultiPlayerGameMasker gameMasker

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    BroadcasterFactory broadcasterFactory

    @Override
    void gameChanged(final MultiPlayerGame game, final Player initiatingPlayer, final boolean initiatingServer) {
        if (broadcasterFactory) {
            game.players.findAll {
                Player p ->
                    initiatingPlayer == null || p != initiatingPlayer
            }.each {
                Player publish ->
                    Broadcaster broadcaster = getBroadcasterFactory().lookup(LiveFeedService.PATH_ROOT + publish.idAsString)
                    if (broadcaster != null) {
                        broadcaster.broadcast(
                                new WebSocketMessage(
                                        messageType: WebSocketMessage.MessageType.Game,
                                        game: gameMasker.maskGameForPlayer((MultiPlayerGame) game, publish)
                                )
                        )
                    }
            }
        }
    }

    @Override
    void playerChanged(final Player player, final boolean initiatingServer) {
        Broadcaster broadcaster = getBroadcasterFactory().lookup(LiveFeedService.PATH_ROOT + player.idAsString)
        if (broadcaster != null) {
            broadcaster.broadcast(
                    new WebSocketMessage(
                            messageType: WebSocketMessage.MessageType.Player,
                            player: player
                    )
            )
        }
    }

    @Override
    void allPlayersChanged(final boolean initiatingServer) {
        getBroadcasterFactory().lookupAll().each {
            Broadcaster broadcaster ->
                Player p = (Player) playerRepository.findOne(stringToIDConverter.convert(broadcaster.ID.replace('/livefeed/', '')))
                if (p) {
                    broadcaster.broadcast(new WebSocketMessage(messageType: WebSocketMessage.MessageType.Player, player: p))
                }
        }
    }

    protected BroadcasterFactory getBroadcasterFactory() {
        if (!broadcasterFactory) {
            broadcasterFactory = Universe.broadcasterFactory()
        }
        broadcasterFactory
    }
}
