package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import groovy.transform.CompileStatic
import org.bson.types.ObjectId

/**
 * Date: 12/16/14
 * Time: 11:42 PM
 */
@CompileStatic
class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    ObjectId deserialize(
            final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new ObjectId(jp.valueAsString)
    }
}
