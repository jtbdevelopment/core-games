package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GameListener
import com.jtbdevelopment.games.publish.PlayerListener

/**
 * Date: 2/17/15
 * Time: 7:10 AM
 */
abstract class AbstractUpdatesToClusterPublisher implements GameListener, PlayerListener {
    abstract protected void internalGameChanged(final Game game, final Player initiatingPlayer)

    abstract protected void internalPlayerChanged(final Player player)

    abstract protected void internalAllPlayersChanged()

    @Override
    void gameChanged(final Game game, final Player initiatingPlayer, final boolean initiatingServer) {
        if (initiatingServer) {
            internalGameChanged(game, initiatingPlayer)
        }
    }

    @Override
    void playerChanged(final Player player, final boolean initiatingServer) {
        if (initiatingServer) {
            internalPlayerChanged(player)
        }
    }

    @Override
    void allPlayersChanged(final boolean initiatingServer) {
        if (initiatingServer) {
            internalAllPlayersChanged()
        }
    }
}
