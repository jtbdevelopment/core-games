package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.games.MultiPlayerGame
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GamePublisher
import com.jtbdevelopment.games.publish.PlayerPublisher
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
    AbstractPlayerRepository playerRepository

    @Autowired(required = false)
    AbstractMultiPlayerGameRepository gameRepository

    protected void receiveClusterMessage(final ClusterMessage clusterMessage) {
        switch (clusterMessage.clusterMessageType) {
            case ClusterMessage.ClusterMessageType.GameUpdate:
                receivePublishGame(clusterMessage.gameId, clusterMessage.playerId)
                break;
            case ClusterMessage.ClusterMessageType.PlayerUpdate:
                receivePublishPlayer(clusterMessage.playerId)
                break;
            case ClusterMessage.ClusterMessageType.AllPlayersUpdate:
                receivePublishAllPlayers()
                break;
        }
    }

    protected void receivePublishAllPlayers() {
        playerPublisher.publishAll(false)
    }

    protected void receivePublishPlayer(final String id) {
        Player p = (Player) playerRepository.findOne(id)
        if (p) {
            playerPublisher.publish(p, false)
        }
    }

    protected void receivePublishGame(final String gameId, final String playerId) {
        if (gameRepository) {
            Player p = (Player) playerRepository.findOne(playerId)
            MultiPlayerGame g = (MultiPlayerGame) gameRepository.findOne(gameId)
            if (p != null && g != null) {
                gamePublisher.publish(g, p, false)
            }
        }
    }
}
