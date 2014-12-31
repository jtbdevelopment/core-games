package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.games.players.SystemPlayer
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 12/30/2014
 * Time: 1:23 PM
 */
@Document(collection = "player")
@CompileStatic
class MongoSystemPlayer extends MongoPlayer implements SystemPlayer<ObjectId> {

    public MongoSystemPlayer() {
        super.source = SYSTEM_SOURCE
    }
}
