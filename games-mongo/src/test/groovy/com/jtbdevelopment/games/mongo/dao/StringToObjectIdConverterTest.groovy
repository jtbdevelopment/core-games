package com.jtbdevelopment.games.mongo.dao

import org.bson.types.ObjectId

/**
 * Date: 3/7/15
 * Time: 3:03 PM
 */
class StringToObjectIdConverterTest extends GroovyTestCase {
    StringToObjectIdConverter converter = new StringToObjectIdConverter()

    void testConvertNull() {
        assertNull converter.convert(null)
    }

    void testConvert() {
        ObjectId objectId = new ObjectId()
        assert objectId == converter.convert(objectId.toHexString())
    }

    void testConvertFail() {
        shouldFail({
            converter.convert('random string')
        })
    }
}
