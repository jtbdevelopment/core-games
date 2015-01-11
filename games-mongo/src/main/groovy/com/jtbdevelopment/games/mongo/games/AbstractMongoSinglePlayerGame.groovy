package com.jtbdevelopment.games.mongo.games

import com.jtbdevelopment.games.games.AbstractSinglePlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

/**
 * Date: 1/9/15
 * Time: 10:19 PM
 */
@CompileStatic
abstract class AbstractMongoSinglePlayerGame extends AbstractSinglePlayerGame<ObjectId> {
    @Id
    ObjectId id

    String getIdAsString() {
        return id?.toHexString()
    }
}
