package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.jtbdevelopment.spring.jackson.AutoRegistrableJsonDeserializer
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 12/16/14
 * Time: 11:42 PM
 */
@CompileStatic
@Component
class ObjectIdDeserializer extends AutoRegistrableJsonDeserializer<ObjectId> {
    @Override
    ObjectId deserialize(
            final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new ObjectId(jp.valueAsString)
    }

    @Override
    Class<ObjectId> registerForClass() {
        return ObjectId.class
    }
}
