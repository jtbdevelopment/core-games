package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.databind.module.SimpleModule
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player

/**
 * Date: 2/8/15
 * Time: 3:49 PM
 */
class MongoPlayerJacksonRegistrationTest extends GroovyTestCase {
    void testCustomizeModule() {
        MongoPlayerJacksonRegistration registration = new MongoPlayerJacksonRegistration()
        boolean registered = false
        def module = [
                addAbstractTypeMapping: {
                    Class iface, Class impl ->
                        assert Player.class.is(iface)
                        assert MongoPlayer.class.is(impl)
                        registered = true
                        return null
                }
        ] as SimpleModule
        registration.customizeModule(module)
        assert registered
    }
}
