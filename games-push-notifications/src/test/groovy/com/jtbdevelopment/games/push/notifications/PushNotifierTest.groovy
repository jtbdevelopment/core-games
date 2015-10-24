package com.jtbdevelopment.games.push.notifications

import com.jtbdevelopment.games.GameCoreTestCase
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
}
