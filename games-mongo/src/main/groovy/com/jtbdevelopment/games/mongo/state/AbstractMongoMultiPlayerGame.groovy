package com.jtbdevelopment.games.mongo.state

import com.jtbdevelopment.games.state.AbstractMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

/**
 * Date: 1/9/15
 * Time: 10:21 PM
 */
@CompileStatic
abstract class AbstractMongoMultiPlayerGame<FEATURES> extends AbstractMultiPlayerGame<ObjectId, FEATURES> {
    @Id
    ObjectId id

    String getIdAsString() {
        return id?.toHexString()
    }
}
