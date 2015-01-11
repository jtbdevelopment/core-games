package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.bson.types.ObjectId

/**
 * Date: 11/9/14
 * Time: 3:44 PM
 */
class MongoPlayerTest extends MongoGameCoreTestCase {

    void testIdAsString() {
        assert PONE.id.toHexString() == PONE.idAsString
    }

    void testIdAsStringNullId() {
        MongoPlayer p = makeSimplePlayer("677")
        p.id = null
        assertNull p.idAsString
    }

    void testEquals() {
        assert PONE.equals(PONE)
        assertFalse PONE.equals(PTWO)
        assert PONE.equals(new MongoPlayer(id: PONE.id))
        assertFalse PONE.equals("String")
        assertFalse PONE.equals(null)
    }

    void testHashCode() {
        def SOMEID = new ObjectId("1234".padRight(24, "0"))
        MongoPlayer player = new MongoPlayer(id: SOMEID)
        assert SOMEID.toHexString().hashCode() == player.hashCode()
    }

    void testToString() {
        assert new MongoPlayer(
                id: new ObjectId("0a123".padRight(24, "0")),
                disabled: false,
                displayName: "BAYMAX",
                sourceId: "BAYMAX",
                source: "BIG HERO 6").toString() == "Player{id='0a1230000000000000000000', source='BIG HERO 6', sourceId='BAYMAX', displayName='BAYMAX', disabled=false}"
    }

    void testMD5() {
        assert PONE.md5 == "ee02ab36f4f4b92d0a2316022a11cce2"
    }

    void testMD5FromBlank() {
        MongoPlayer p = new MongoPlayer()
        assert p.md5 == ""
    }
}
