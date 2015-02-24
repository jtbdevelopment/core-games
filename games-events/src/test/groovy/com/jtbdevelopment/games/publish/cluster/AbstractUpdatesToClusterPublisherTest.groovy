package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.players.Player

/**
 * Date: 2/24/15
 * Time: 12:02 PM
 */
class AbstractUpdatesToClusterPublisherTest extends GroovyTestCase {
    private static class ClusterPublisher extends AbstractUpdatesToClusterPublisher {
        Player player = null
        Game game = null
        boolean allPlayers = false

        @Override
        protected void internalGameChanged(final Game game, final Player initiatingPlayer) {
            this.game = game
            this.player = initiatingPlayer
            this.allPlayers = false
        }

        @Override
        protected void internalPlayerChanged(final Player player) {
            this.game = null
            this.player = player
            this.allPlayers = false
        }

        @Override
        protected void internalAllPlayersChanged() {
            this.game = null
            this.player = null
            this.allPlayers = true
        }
    }

    ClusterPublisher publisher = new ClusterPublisher()

    void testPublishGameFromThisServer() {
        Game g = [] as Game
        Player p = [] as Player

        publisher.gameChanged(g, p, true)
        assert publisher.game.is(g)
        assert publisher.player.is(p)
        assertFalse publisher.allPlayers
    }

    void testPublishGameNotFromThisServer() {
        Game g = [] as Game
        Player p = [] as Player

        publisher.gameChanged(g, p, false)
        assertNull publisher.game
        assertNull publisher.player
        assertFalse publisher.allPlayers
    }

    void testPublishPlayerFromThisServer() {
        Player p = [] as Player

        publisher.playerChanged(p, true)
        assertNull publisher.game
        assert publisher.player.is(p)
        assertFalse publisher.allPlayers
    }

    void testPublishPlayerNotFromThisServer() {
        Player p = [] as Player

        publisher.playerChanged(p, false)
        assertNull publisher.game
        assertNull publisher.player
        assertFalse publisher.allPlayers
    }

    void testPublishAllPlayerFromThisServer() {
        publisher.allPlayersChanged(true)
        assertNull publisher.game
        assertNull publisher.player
        assert publisher.allPlayers
    }

    void testPublishAllPlayerNotFromThisServer() {
        publisher.allPlayersChanged(false)
        assertNull publisher.game
        assertNull publisher.player
        assertFalse publisher.allPlayers
    }
}
