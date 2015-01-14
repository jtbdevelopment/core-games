package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializerProvider
import com.jtbdevelopment.spring.jackson.AutoRegistrableJsonSerializer
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 12/16/14
 * Time: 11:42 PM
 */
@CompileStatic
@Component
class ObjectIdSerializer extends AutoRegistrableJsonSerializer<ObjectId> {
    @Override
    void serialize(
            final ObjectId value,
            final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(value.toHexString())
    }

    @Override
    Class<ObjectId> registerForClass() {
        return ObjectId.class
    }
}
