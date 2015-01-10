package com.jtbdevelopment.games.mongo.games

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

/**
 * Date: 1/9/15
 * Time: 10:47 PM
 */
class AbstractMongoSinglePlayerGameTest extends GroovyTestCase {
    private static class AGame extends AbstractMongoSinglePlayerGame {

    }

    void testIdAnnotation() {
        assert AbstractMongoSinglePlayerGame.class.getDeclaredField('id').isAnnotationPresent(Id.class)
    }

    void testIdAsString() {
        ObjectId id = new ObjectId()
        AGame game = new AGame(id: id)
        assert id.is(game.id)
        assert id.toHexString() == game.idAsString
    }
}
