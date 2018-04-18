package com.jtbdevelopment.games.mongo.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.jtbdevelopment.core.spring.jackson.AutoRegistrableJsonDeserializer;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 12/16/14 Time: 11:42 PM
 */
@Component
public class ObjectIdDeserializer extends AutoRegistrableJsonDeserializer<ObjectId> {

  @Override
  public ObjectId deserialize(final JsonParser jp, final DeserializationContext ctxt)
      throws IOException {
    return new ObjectId(jp.getValueAsString());
  }

  @Override
  public Class<ObjectId> handledType() {
    return ObjectId.class;
  }

}
