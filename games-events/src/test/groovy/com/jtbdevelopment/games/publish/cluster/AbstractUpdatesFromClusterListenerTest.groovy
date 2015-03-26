package com.jtbdevelopment.games.publish.cluster

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.games.MultiPlayerGame
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GamePublisher
import com.jtbdevelopment.games.publish.PlayerPublisher

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
                findOne: {
                    String id ->
                        assert id == PTWO.idAsString.reverse()
                        return PTWO
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
                findOne: {
                    String id ->
                        assert id == PTHREE.idAsString.reverse()
                        return PTHREE
                }
        ] as AbstractPlayerRepository
        listener.gameRepository = [
                findOne: {
                    String id ->
                        assert id == gameId.reverse()
                        return game
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

    void testReceivePublishGameWithNoRepository() {
        String gameId = 'GID'
        boolean published = false
        MultiPlayerGame game = [] as MultiPlayerGame
        listener.playerRepository = [
                findOne: {
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
