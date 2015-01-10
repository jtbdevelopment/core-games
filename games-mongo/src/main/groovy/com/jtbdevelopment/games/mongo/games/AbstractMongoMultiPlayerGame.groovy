package com.jtbdevelopment.games.mongo.games

import com.jtbdevelopment.games.games.AbstractMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

/**
 * Date: 1/9/15
 * Time: 10:21 PM
 */
@CompileStatic
abstract class AbstractMongoMultiPlayerGame extends AbstractMultiPlayerGame<ObjectId> {
    @Id
    ObjectId id

    String getIdAsString() {
        return id.toHexString()
    }
}
