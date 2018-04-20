package com.jtbdevelopment.games.mongo.players

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.bson.types.ObjectId
import org.junit.Test
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import java.lang.reflect.Field

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNull

/**
 * Date: 11/9/14
 * Time: 3:44 PM
 */
class MongoPlayerTest extends MongoGameCoreTestCase {

    @Test
    void testClassAnnotations() {
        assert MongoPlayer.class.isAnnotationPresent(Document.class)
        assert MongoPlayer.class.getAnnotation(Document.class).collection() == 'player'
        assert MongoPlayer.class.isAnnotationPresent(JsonIgnoreProperties.class)
        assert MongoPlayer.class.getAnnotation(JsonIgnoreProperties.class).value() == ['idAsString', 'sourceAndSourceId']
    }

    @Test
    void testIdAnnotations() {
        Field f = MongoPlayer.getDeclaredField('id')
        assert f.getAnnotation(Id.class)
    }

    @Test
    void testMd5Annotations() {
        Field f = MongoPlayer.getDeclaredField('md5')
        assert f.getAnnotation(Indexed.class)
    }

    @Test
    void testIdAsString() {
        assert PONE.id.toHexString() == PONE.idAsString
    }

    @Test
    void testIdAsStringNullId() {
        MongoPlayer p = makeSimplePlayer("677")
        p.id = null
        assertNull p.idAsString
    }

    @Test
    void testEquals() {
        assert PONE.equals(PONE)
        assertFalse PONE.equals(PTWO)
        assert PONE.equals(new MongoPlayer(id: PONE.id))
        assertFalse PONE.equals("String")
        assertFalse PONE.equals(null)
    }

    @Test
    void testHashCode() {
        def SOMEID = new ObjectId("1234".padRight(24, "0"))
        MongoPlayer player = new MongoPlayer(id: SOMEID)
        assert SOMEID.toHexString().hashCode() == player.hashCode()
    }

    @Test
    void testToString() {
        assert new MongoPlayer(
                id: new ObjectId("0a123".padRight(24, "0")),
                disabled: false,
                displayName: "BAYMAX",
                sourceId: "BAYMAX",
                source: "BIG HERO 6").toString() == "Player{id='0a1230000000000000000000', source='BIG HERO 6', sourceId='BAYMAX', displayName='BAYMAX', disabled=false}"
    }

    @Test
    void testMD5() {
        assert PONE.md5 == "196c643ff2d27ff53cbd574c08c7726f"
    }

    @Test
    void testMD5FromBlank() {
        MongoPlayer p = new MongoPlayer()
        assert p.md5 == ""
    }
}
