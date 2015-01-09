package com.jtbdevelopment.games.mongo.players

import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 12/16/14
 * Time: 7:00 AM
 */
class MongoManualPlayerTest extends GroovyTestCase {

    void testClassAnnotations() {
        assert MongoManualPlayer.class.getAnnotation(Document.class).collection() == 'player'
    }

    void testSourceDefaults() {
        MongoManualPlayer p = new MongoManualPlayer()

        assert p.source == MongoManualPlayer.MANUAL_SOURCE
    }
}
