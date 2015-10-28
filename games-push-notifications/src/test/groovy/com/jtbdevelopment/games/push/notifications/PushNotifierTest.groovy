package com.jtbdevelopment.games.push.notifications

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.notifications.RegisteredDevice

import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.GenericType

/**
 * Date: 10/18/2015
 * Time: 9:05 PM
 */
class PushNotifierTest extends GameCoreTestCase {
    PushNotifier notifier = new PushNotifier()

    void testSimpleCase() {
        notifier.baseMessage = [
                test: "value"
        ]
        def result = [canonical_ids: 0, failure: 0]
        notifier.builder = [
                post: {
                    Entity e, GenericType t ->
                        assert ((Map<String, Object>) e.entity) == [
                                test: "value", registration_ids: ["dev1", "dev2"]
                        ]
                        return result
                }
        ] as Invocation.Builder

        Player player = new GameCoreTestCase.StringPlayer(registeredDevices: [new RegisteredDevice(deviceID: "dev1"), new RegisteredDevice(deviceID: "dev2")] as Set)
        assert notifier.notifyPlayer(player, null)
    }

    void testCaseWhereWeReceiveVaryingResponsesOnStatus() {
        notifier.baseMessage = [
                test: "value"
        ]
        def result = [
                canonical_ids: 1,
                failure      : 3,
                results      : [
                        [message_id: '123'],
                        [registration_id: 'newid', message_id: 'xa'],
                        [error: 'Unavailable'],
                        [error: 'NotRegistered'],
                        [error: 'InvalidRegistration']
                ]
        ]
        notifier.builder = [
                post: {
                    Entity e, GenericType t ->
                        assert ((Map<String, Object>) e.entity) == [
                                test: "value", registration_ids: ["good1", "old1", "unavailable", "notreg", "invalid"]
                        ]
                        return result
                }
        ] as Invocation.Builder

        Player player = new GameCoreTestCase.StringPlayer(
                registeredDevices: [
                        new RegisteredDevice(deviceID: "good1"),
                        new RegisteredDevice(deviceID: "old1"),
                        new RegisteredDevice(deviceID: "unavailable"),
                        new RegisteredDevice(deviceID: "notreg"),
                        new RegisteredDevice(deviceID: "invalid"),
                ] as Set)
        Player saved = new GameCoreTestCase.StringPlayer()
        boolean savedCalled = false
        notifier.playerRepository = [
                save: {
                    Player p ->
                        assert p.is(player)
                        assert 3 == p.registeredDevices.size()
                        assert ["good1", "newid", "unavailable"] as Set == p.registeredDevices.collect {
                            it.deviceID
                        } as Set
                        savedCalled = true
                        return saved
                }
        ] as AbstractPlayerRepository
        assert notifier.notifyPlayer(player, null)
        assert savedCalled
    }
}
