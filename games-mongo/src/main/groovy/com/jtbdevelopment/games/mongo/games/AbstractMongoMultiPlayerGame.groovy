package com.jtbdevelopment.games.mongo.games

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.jtbdevelopment.games.games.AbstractMultiPlayerGame
import com.jtbdevelopment.games.mongo.json.ObjectIdDeserializer
import com.jtbdevelopment.games.mongo.json.ObjectIdSerializer
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
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    ObjectId id

    String getIdAsString() {
        return id.toHexString()
    }
}
