package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.core.JsonGenerator
import org.bson.types.ObjectId

/**
 * Date: 12/22/14
 * Time: 12:04 PM
 */
class ObjectIdSerializerTest extends GroovyTestCase {
    private ObjectIdSerializer serializer = new ObjectIdSerializer()

    void testSerializesHexString() {
        ObjectId id = new ObjectId()
        def jgen = [
                writeString: {
                    String it ->
                        assert it == id.toHexString()
                }
        ] as JsonGenerator
        serializer.serialize(id, jgen, null)
    }

    void testGetRegisterForClass() {
        assert ObjectId.class.is(serializer.registerForClass())
    }
}
