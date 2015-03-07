package com.jtbdevelopment.games.mongo.dao

import org.bson.types.ObjectId

/**
 * Date: 3/7/15
 * Time: 3:06 PM
 */
class ObjectIDToStringConverterTest extends GroovyTestCase {
    ObjectIDToStringConverter converter = new ObjectIDToStringConverter()

    void testConvertNull() {
        assertNull converter.convert(null)
    }

    void testConvert() {
        ObjectId objectId = new ObjectId()
        assert objectId.toHexString() == converter.convert(objectId)
    }
}
