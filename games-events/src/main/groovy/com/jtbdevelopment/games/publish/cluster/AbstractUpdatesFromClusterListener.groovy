package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.PlayerPublisher
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 2/17/15
 * Time: 6:56 AM
 */
@CompileStatic
abstract class AbstractUpdatesFromClusterListener {
    @Autowired
    GamePublisher gamePublisher

    @Autowired
    PlayerPublisher playerPublisher

    @Autowired
    AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    @Autowired(required = false)
    AbstractGameRepository<? extends Serializable, ?, ?, ? extends Game> gameRepository

    protected void receiveClusterMessage(final ClusterMessage clusterMessage) {
        switch (clusterMessage.clusterMessageType) {
            case ClusterMessage.ClusterMessageType.GameUpdate:
                receivePublishGame(clusterMessage.gameId, clusterMessage.playerId)
                break
            case ClusterMessage.ClusterMessageType.PlayerUpdate:
                receivePublishPlayer(clusterMessage.playerId)
                break
            case ClusterMessage.ClusterMessageType.AllPlayersUpdate:
                receivePublishAllPlayers()
                break
        }
    }

    protected void receivePublishAllPlayers() {
        playerPublisher.publishAll(false)
    }

    protected void receivePublishPlayer(final String id) {
        def optional = playerRepository.findById((Serializable) stringToIDConverter.convert(id))
        if (optional.present) {
            playerPublisher.publish(optional.get(), false)
        }
    }

    protected void receivePublishGame(final String gameId, final String playerId) {
        if (gameRepository) {
            def optionalPlayer = playerRepository.findById(stringToIDConverter.convert(playerId))
            if (optionalPlayer.present || playerId == null) {
                def optionalGame = gameRepository.findById(stringToIDConverter.convert(gameId))
                if (optionalGame.present) {
                    gamePublisher.publish(
                            optionalGame.get(),
                            optionalPlayer.present ? optionalPlayer.get() : null,
                            false)
                }
            }
        }
    }
}
