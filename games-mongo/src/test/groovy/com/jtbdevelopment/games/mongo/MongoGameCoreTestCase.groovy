package com.jtbdevelopment.games.mongo

import com.jtbdevelopment.games.mongo.players.MongoPlayer
import org.bson.types.ObjectId

/**
 * Date: 12/30/2014
 * Time: 2:00 PM
 */
abstract class MongoGameCoreTestCase extends GroovyTestCase {
    protected static final MongoPlayer PONE = makeSimplePlayer("1")
    protected static final MongoPlayer PTWO = makeSimplePlayer("2")
    protected static final MongoPlayer PTHREE = makeSimplePlayer("3")
    protected static final MongoPlayer PFOUR = makeSimplePlayer("4")
    protected static final MongoPlayer PFIVE = makeSimplePlayer("5")
    protected static final MongoPlayer PINACTIVE1 = makeSimplePlayer("A1", true)
    protected static final MongoPlayer PINACTIVE2 = makeSimplePlayer("A2", true)

    protected static MongoPlayer makeSimplePlayer(final String id, final boolean disabled = false) {
        return new MongoPlayer(
                id: new ObjectId(id.padRight(24, "0")),
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id)
    }
}
