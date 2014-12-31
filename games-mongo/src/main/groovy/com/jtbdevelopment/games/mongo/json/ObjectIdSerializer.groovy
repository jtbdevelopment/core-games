package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import groovy.transform.CompileStatic
import org.bson.types.ObjectId

/**
 * Date: 12/16/14
 * Time: 11:42 PM
 */
@CompileStatic
class ObjectIdSerializer extends JsonSerializer<ObjectId> {
    @Override
    void serialize(
            final ObjectId value,
            final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(value.toHexString())
    }
}
