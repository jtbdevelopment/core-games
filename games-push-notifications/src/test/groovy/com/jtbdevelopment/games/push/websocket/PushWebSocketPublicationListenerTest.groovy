package com.jtbdevelopment.games.push.websocket

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker
import com.jtbdevelopment.games.state.MultiPlayerGame

import java.util.concurrent.ConcurrentHashMap

/**
 * Date: 10/11/2015
 * Time: 8:59 PM
 */
class PushWebSocketPublicationListenerTest extends GameCoreTestCase {
    PushWebSocketPublicationListener listener = new PushWebSocketPublicationListener()

    void testSetsValueForTruePublishWhenNotSet() {
        listener.trackingMap = new ConcurrentHashMap<>();
        Player player = new GameCoreTestCase.StringPlayer(id: "X")
        MultiPlayerGame game = new GameCoreTestCase.StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, true)
        assert 1 == listener.trackingMap.size()
        assert listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }

    void testSetsValueForFalsePublishWhenNotSet() {
        listener.trackingMap = new ConcurrentHashMap<>();
        Player player = new GameCoreTestCase.StringPlayer(id: "X")
        MultiPlayerGame game = new GameCoreTestCase.StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, false)
        assert 1 == listener.trackingMap.size()
        assertFalse listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }

    void testSetsValueForTruePublishWhenAlreadySetToFalse() {
        listener.trackingMap = new ConcurrentHashMap<>();
        Player player = new GameCoreTestCase.StringPlayer(id: "X")
        MultiPlayerGame game = new GameCoreTestCase.StringMPGame(id: "Y")
        listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)] = false
        listener.publishedGameUpdateToPlayer(player, game, true)
        assert 1 == listener.trackingMap.size()
        assert listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }

    void testDoesNotSetValueForFalsePublishWhenAlreadySetToTrue() {
        listener.trackingMap = new ConcurrentHashMap<>();
        Player player = new GameCoreTestCase.StringPlayer(id: "X")
        MultiPlayerGame game = new GameCoreTestCase.StringMPGame(id: "Y")
        listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)] = true
        listener.publishedGameUpdateToPlayer(player, game, false)
        assert 1 == listener.trackingMap.size()
        assert listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }
}
