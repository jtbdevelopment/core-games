package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.dao.StringToIDConverter
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 3/7/15
 * Time: 3:02 PM
 */
@CompileStatic
@Component
class StringToObjectIdConverter implements StringToIDConverter<ObjectId> {
    @Override
    ObjectId convert(final String source) {
        return source ? new ObjectId(source) : null
    }
}
