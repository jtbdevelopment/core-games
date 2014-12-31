package com.jtbdevelopment.games.mongo.players

/**
 * Date: 12/16/14
 * Time: 7:00 AM
 */
class MongoManualPlayerTest extends GroovyTestCase {

    void testSourceDefaults() {
        MongoManualPlayer p = new MongoManualPlayer()

        assert p.source == MongoManualPlayer.MANUAL_SOURCE
    }
}
