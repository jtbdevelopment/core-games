package com.jtbdevelopment.games.mongo.state

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

/**
 * Date: 1/9/15
 * Time: 10:41 PM
 */
class AbstractMongoMultiPlayerGameTest extends GroovyTestCase {
    private static class AGame extends AbstractMongoMultiPlayerGame {

    }

    void testIdAnnotation() {
        assert AbstractMongoMultiPlayerGame.class.getDeclaredField('id').isAnnotationPresent(Id.class)
    }

    void testIdAsString() {
        ObjectId id = new ObjectId()
        ObjectId previous = new ObjectId()
        AGame game = new AGame(id: id, previousId: previous)
        assert id.is(game.id)
        assert id.toHexString() == game.idAsString
        assert previous.is(game.previousId)
        assert previous.toHexString() == game.previousIdAsString
    }

    void testIdAsStringNullId() {
        AGame game = new AGame()
        assertNull game.id
        assertNull game.idAsString
        assertNull game.previousId
        assertNull game.previousIdAsString
    }
}
