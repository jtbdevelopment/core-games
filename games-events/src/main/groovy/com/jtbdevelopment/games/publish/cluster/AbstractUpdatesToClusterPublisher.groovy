package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GameListener
import com.jtbdevelopment.games.publish.PlayerListener
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 2/17/15
 * Time: 7:10 AM
 */
@CompileStatic
abstract class AbstractUpdatesToClusterPublisher implements GameListener<Game>, PlayerListener {

    abstract protected void internalPublish(final ClusterMessage clusterMessage)

    @Override
    void gameChanged(final Game game, final Player initiatingPlayer, final boolean initiatingServer) {
        if (initiatingServer) {
            internalPublish(new ClusterMessage(
                    gameId: game.idAsString,
                    playerId: initiatingPlayer?.idAsString,
                    clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate)
            )
        }
    }

    @Override
    void playerChanged(final Player player, final boolean initiatingServer) {
        if (initiatingServer) {
            internalPublish(new ClusterMessage(
                    playerId: player.idAsString,
                    clusterMessageType: ClusterMessage.ClusterMessageType.PlayerUpdate)
            )
        }
    }

    @Override
    void allPlayersChanged(final boolean initiatingServer) {
        if (initiatingServer) {
            internalPublish(new ClusterMessage(
                    clusterMessageType: ClusterMessage.ClusterMessageType.AllPlayersUpdate)
            )
        }
    }
}
