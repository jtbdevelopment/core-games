package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GamePublisher
import com.jtbdevelopment.games.publish.PlayerPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 2/17/15
 * Time: 6:56 AM
 *
 * TODO - actually hook up to something
 */
@Component
class UpdatesFromClusterListener {
    @Autowired
    GamePublisher gamePublisher

    @Autowired
    PlayerPublisher playerPublisher

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    AbstractGameRepository gameRepository

    void receivePublishAllPlayers() {
        //  TODO - clear cache
        playerPublisher.publishAll(false)
    }

    void receivePublishPlayer(final String id) {
        //  TODO - clear cache
        Player p = (Player) playerRepository.findOne(id)
        if (p) {
            playerPublisher.publish(p, false)
        }
    }

    void receivePublishGame(final String gameId, final String playerId) {
        //  TODO - clear cache
        Player p = (Player) playerRepository.findOne(playerId)
        Game g = (Game) gameRepository.findOne(gameId)
        if (p != null && g != null) {
            gamePublisher.publish(g, p, false)
        }
    }
}
