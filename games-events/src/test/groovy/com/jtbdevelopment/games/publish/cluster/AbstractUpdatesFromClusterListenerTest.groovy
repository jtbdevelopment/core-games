package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.PlayerPublisher
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.MultiPlayerGame

/**
 * Date: 2/21/15
 * Time: 6:24 PM
 */
class AbstractUpdatesFromClusterListenerTest extends GameCoreTestCase {
    private AbstractUpdatesFromClusterListener listener = new AbstractUpdatesFromClusterListener() {}

    @Override
    protected void setUp() throws Exception {
        listener.stringToIDConverter = new GameCoreTestCase.StringToStringConverter()
    }

    void testReceivePublishAllPlayers() {
        boolean published = false
        listener.playerPublisher = [
                publishAll: {
                    boolean fromServer ->
                        assertFalse fromServer
                        published = true
                }
        ] as PlayerPublisher
        listener.receiveClusterMessage(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.AllPlayersUpdate))
        assert published
    }

    void testReceivePublishPlayer() {
        boolean published = false
        listener.playerRepository = [
                findById: {
                    String id ->
                        assert id == PTWO.idAsString.reverse()
                        return Optional.of(PTWO)
                }
        ] as AbstractPlayerRepository
        listener.playerPublisher = [
                publish: {
                    Player p, boolean fromServer ->
                        assert p.is(PTWO)
                        assertFalse fromServer
                        published = true
                }
        ] as PlayerPublisher
        listener.receiveClusterMessage(new ClusterMessage(
                clusterMessageType: ClusterMessage.ClusterMessageType.PlayerUpdate,
                playerId: PTWO.idAsString)
        )
        assert published
    }

    void testReceivePublishGame() {
        String gameId = 'GID'
        boolean published = false
        MultiPlayerGame game = [] as MultiPlayerGame
        listener.playerRepository = [
                findById: {
                    String id ->
                        assert id == PTHREE.idAsString.reverse()
                        return Optional.of(PTHREE)
                }
        ] as AbstractPlayerRepository
        listener.gameRepository = [
                findById: {
                    String id ->
                        assert id == gameId.reverse()
                        return Optional.of(game)
                }
        ] as AbstractMultiPlayerGameRepository
        listener.gamePublisher = [
                publish: {
                    Game g, Player p, boolean fromServer ->
                        assert p.is(PTHREE)
                        assert g.is(game)
                        assertFalse fromServer
                        published = true
                        return g
                }
        ] as GamePublisher
        listener.receiveClusterMessage(new ClusterMessage(
                clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate,
                playerId: PTHREE.idAsString,
                gameId: gameId)
        )
        assert published
    }

    void testReceivePublishGameNullPlayer() {
        String gameId = 'GID'
        boolean published = false
        MultiPlayerGame game = [] as MultiPlayerGame
        listener.playerRepository = [
                findById: {
                    String id ->
                        assertNull id
                        return Optional.empty()
                }
        ] as AbstractPlayerRepository
        listener.gameRepository = [
                findById: {
                    String id ->
                        assert id == gameId.reverse()
                        return Optional.of(game)
                }
        ] as AbstractMultiPlayerGameRepository
        listener.gamePublisher = [
                publish: {
                    Game g, Player p, boolean fromServer ->
                        assertNull p
                        assert g.is(game)
                        assertFalse fromServer
                        published = true
                        return g
                }
        ] as GamePublisher
        listener.receiveClusterMessage(new ClusterMessage(
                clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate,
                playerId: null,
                gameId: gameId)
        )
        assert published
    }

    void testReceivePublishGameInvalidPlayer() {
        String gameId = 'GID'
        boolean published = false
        MultiPlayerGame game = [] as MultiPlayerGame
        listener.playerRepository = [
                findById: {
                    String id ->
                        assert id == PINACTIVE1.id
                        return Optional.empty()
                }
        ] as AbstractPlayerRepository
        listener.gameRepository = null
        listener.gamePublisher = null
        listener.receiveClusterMessage(new ClusterMessage(
                clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate,
                playerId: PINACTIVE1.id,
                gameId: gameId)
        )
        assertFalse published
    }

    void testReceivePublishGameWithNoGameRepository() {
        String gameId = 'GID'
        boolean published = false
        MultiPlayerGame game = [] as MultiPlayerGame
        listener.playerRepository = [
                findById: {
                    String id ->
                        assert id == PTHREE.idAsString
                        return PTHREE
                }
        ] as AbstractPlayerRepository
        listener.gameRepository = null
        listener.gamePublisher = [
                publish: {
                    Game g, Player p, boolean fromServer ->
                        assert p.is(PTHREE)
                        assert g.is(game)
                        assertFalse fromServer
                        published = true
                        return g
                }
        ] as GamePublisher
        listener.receiveClusterMessage(new ClusterMessage(
                clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate,
                playerId: PTHREE.idAsString,
                gameId: gameId)
        )
        assertFalse published
    }
}
