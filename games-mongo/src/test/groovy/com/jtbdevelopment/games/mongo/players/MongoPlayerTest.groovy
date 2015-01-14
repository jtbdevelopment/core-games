package com.jtbdevelopment.games.mongo.players

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import java.lang.reflect.Field

/**
 * Date: 11/9/14
 * Time: 3:44 PM
 */
class MongoPlayerTest extends MongoGameCoreTestCase {

    void testClassAnnotations() {
        assert MongoPlayer.class.isAnnotationPresent(Document.class)
        assert MongoPlayer.class.getAnnotation(Document.class).collection() == 'player'
        assert MongoPlayer.class.isAnnotationPresent(JsonIgnoreProperties.class)
        assert MongoPlayer.class.getAnnotation(JsonIgnoreProperties.class).value() == ['idAsString']
    }

    void testIdAnnotations() {
        Field f = MongoPlayer.getDeclaredField('id')
        assert f.getAnnotation(Id.class)
    }

    void testMd5Annotations() {
        Field f = MongoPlayer.getDeclaredField('md5')
        assert f.getAnnotation(Indexed.class)
    }

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
