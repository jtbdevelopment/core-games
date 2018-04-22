package com.jtbdevelopment.games.push.websocket

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.map.listener.MapListener
import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.notifications.RegisteredDevice
import com.jtbdevelopment.games.push.PushProperties
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.stringimpl.StringMPGame
import com.jtbdevelopment.games.stringimpl.StringPlayer

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Date: 10/11/2015
 * Time: 8:59 PM
 */
class PushWebSocketPublicationListenerTest extends GameCoreTestCase {
    PushWebSocketPublicationListener listener = new PushWebSocketPublicationListener()

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        PushWebSocketPublicationListener.computeRegistrationCutoff()
        listener.pushProperties = new PushProperties(enabled: true)
    }

    void testSetupWithEnabledPushProperties() {
        boolean listenerRegistered = false
        listener.pushNotifierFilter = [] as PushNotifierFilter
        listener.hazelcastInstance = [
                getMap: {
                    String name ->
                        assert "PUSH_TRACKING_MAP" == name
                        return [
                                addEntryListener: {
                                    MapListener l, boolean f ->
                                        assert f
                                        assert l.is(listener.pushNotifierFilter)
                                        listenerRegistered = true
                                        return "SOMEUUID"
                                }
                        ] as IMap
                }
        ] as HazelcastInstance
        listener.setup()
        assert listenerRegistered
    }

    void testSetupWithDisabledPushProperties() {
        boolean listenerRegistered = false
        listener.pushProperties.enabled = false
        listener.setup()
        assertFalse listenerRegistered
    }

    void testIgnoresPlayerWithNoDevices() {
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X")
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, true)
        assert listener.trackingMap.isEmpty()
    }

    void testIgnoresPlayerWithNoDevicesRegistedInLast30Days() {
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X", registeredDevices: [new RegisteredDevice(lastRegistered: ZonedDateTime.now(ZoneId.of("GMT")).minusDays(30).minusSeconds(60).toInstant())])
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, true)
        assert listener.trackingMap.isEmpty()
    }

    void testSetsValueForTruePublishWhenNotSet() {
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X", registeredDevices: [new RegisteredDevice()] as Set)
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, true)
        assert 1 == listener.trackingMap.size()
        assert listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }

    void testSetsValueForFalsePublishWhenNotSet() {
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X", registeredDevices: [new RegisteredDevice()] as Set)
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, false)
        assert 1 == listener.trackingMap.size()
        assertFalse listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }

    void testDoesNotSetsValueForFalsePublishWhenPushDisabled() {
        listener.pushProperties.enabled = false
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X", registeredDevices: [new RegisteredDevice()] as Set)
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.publishedGameUpdateToPlayer(player, game, false)
        assert listener.trackingMap.isEmpty()
    }

    void testSetsValueForTruePublishWhenAlreadySetToFalse() {
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X", registeredDevices: [new RegisteredDevice()] as Set)
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)] = false
        listener.publishedGameUpdateToPlayer(player, game, true)
        assert 1 == listener.trackingMap.size()
        assert listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }

    void testDoesNotSetValueForFalsePublishWhenAlreadySetToTrue() {
        listener.trackingMap = new ConcurrentHashMap<>()
        Player player = new StringPlayer(id: "X", registeredDevices: [new RegisteredDevice()] as Set)
        MultiPlayerGame game = new StringMPGame(id: "Y")
        listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)] = true
        listener.publishedGameUpdateToPlayer(player, game, false)
        assert 1 == listener.trackingMap.size()
        assert listener.trackingMap[new GamePublicationTracker(pid: player.id, gid: game.id)]
    }
}
