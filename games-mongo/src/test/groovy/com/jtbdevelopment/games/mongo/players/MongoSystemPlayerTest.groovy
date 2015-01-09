package com.jtbdevelopment.games.mongo.players

import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 1/8/15
 * Time: 10:11 PM
 */
class MongoSystemPlayerTest extends GroovyTestCase {
    void testClassAnnotations() {
        assert MongoSystemPlayer.class.getAnnotation(Document.class).collection() == 'player'
    }

    void testSourceDefaults() {
        MongoSystemPlayer p = new MongoSystemPlayer()

        assert p.source == MongoSystemPlayer.SYSTEM_SOURCE
    }

}
