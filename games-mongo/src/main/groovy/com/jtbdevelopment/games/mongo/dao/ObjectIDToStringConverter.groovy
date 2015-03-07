package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.dao.IDToStringConverter
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 3/7/15
 * Time: 3:01 PM
 */
@CompileStatic
@Component
class ObjectIDToStringConverter implements IDToStringConverter<ObjectId> {
    @Override
    String convert(final ObjectId source) {
        return source?.toHexString()
    }
}
