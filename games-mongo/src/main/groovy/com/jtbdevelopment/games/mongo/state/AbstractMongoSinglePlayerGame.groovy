package com.jtbdevelopment.games.mongo.state

import com.jtbdevelopment.games.state.AbstractSinglePlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes

/**
 * Date: 1/9/15
 * Time: 10:19 PM
 */
@CompileStatic
//  You need to copy these to derived clas unfortunately
@CompoundIndexes([
        @CompoundIndex(name = "created", def = "{'created': 1}"),
        @CompoundIndex(name = "lastUpdated", def = "{'lastUpdate': 1}"),
])
abstract class AbstractMongoSinglePlayerGame<FEATURES> extends AbstractSinglePlayerGame<ObjectId, FEATURES> {
    @Id
    ObjectId id

    String getIdAsString() {
        return id?.toHexString()
    }
}
