package com.jtbdevelopment.games.mongo.games

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.jtbdevelopment.games.games.AbstractSinglePlayerGame
import com.jtbdevelopment.games.mongo.json.ObjectIdDeserializer
import com.jtbdevelopment.games.mongo.json.ObjectIdSerializer
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
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    ObjectId id

    String getIdAsString() {
        return id.toHexString()
    }
}
