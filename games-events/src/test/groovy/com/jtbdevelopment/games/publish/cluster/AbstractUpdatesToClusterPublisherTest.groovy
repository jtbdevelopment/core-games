package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame

/**
 * Date: 2/24/15
 * Time: 12:02 PM
 */
class AbstractUpdatesToClusterPublisherTest extends GroovyTestCase {
    private static class ClusterPublisher extends AbstractUpdatesToClusterPublisher {
        ClusterMessage clusterMessage = null
        @Override
        protected void internalPublish(final ClusterMessage clusterMessage) {
            this.clusterMessage = clusterMessage
        }

    }

    ClusterPublisher publisher = new ClusterPublisher()

    void testPublishGameFromThisServer() {
        MultiPlayerGame g = [getIdAsString: { return 'G' }] as MultiPlayerGame
        Player p = [getIdAsString: { return 'TTT' }] as Player

        publisher.gameChanged(g, p, true)
        assert publisher.clusterMessage.gameId == g.idAsString
        assert publisher.clusterMessage.playerId == p.idAsString
        assert publisher.clusterMessage.clusterMessageType == ClusterMessage.ClusterMessageType.GameUpdate
    }

    void testPublishGameFromThisServerWithNullPlayer() {
        MultiPlayerGame g = [getIdAsString: { return 'G' }] as MultiPlayerGame

        publisher.gameChanged(g, null, true)
        assert publisher.clusterMessage.gameId == g.idAsString
        assertNull publisher.clusterMessage.playerId
        assert publisher.clusterMessage.clusterMessageType == ClusterMessage.ClusterMessageType.GameUpdate
    }

    void testPublishGameNotFromThisServer() {
        MultiPlayerGame g = [getIdAsString: { return 'G' }] as MultiPlayerGame
        Player p = [getIdAsString: { return 'TTT' }] as Player

        publisher.gameChanged(g, p, false)
        assertNull publisher.clusterMessage
    }

    void testPublishPlayerFromThisServer() {
        Player p = [getIdAsString: { return 'TTT' }] as Player

        publisher.playerChanged(p, true)
        assertNull publisher.clusterMessage.gameId
        assert publisher.clusterMessage.playerId == p.idAsString
        assert publisher.clusterMessage.clusterMessageType == ClusterMessage.ClusterMessageType.PlayerUpdate
    }

    void testPublishPlayerNotFromThisServer() {
        Player p = [] as Player

        publisher.playerChanged(p, false)
        assertNull publisher.clusterMessage
    }

    void testPublishAllPlayerFromThisServer() {
        publisher.allPlayersChanged(true)
        assertNull publisher.clusterMessage.gameId
        assertNull publisher.clusterMessage.playerId
        assert publisher.clusterMessage.clusterMessageType == ClusterMessage.ClusterMessageType.AllPlayersUpdate
    }

    void testPublishAllPlayerNotFromThisServer() {
        publisher.allPlayersChanged(false)
        assertNull publisher.clusterMessage
    }
}
