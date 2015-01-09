package com.jtbdevelopment.games.mongo.players

/**
 * Date: 1/8/15
 * Time: 10:10 PM
 */
class MongoPlayerFactoryTest extends GroovyTestCase {
    MongoPlayerFactory factory = new MongoPlayerFactory()

    void testNewPlayer() {
        assert factory.newPlayer() instanceof MongoPlayer
    }

    void testNewManualPlayer() {
        assert factory.newManualPlayer() instanceof MongoManualPlayer
    }

    void testNewSystemPlayer() {
        assert factory.newSystemPlayer() instanceof MongoSystemPlayer

    }
}
