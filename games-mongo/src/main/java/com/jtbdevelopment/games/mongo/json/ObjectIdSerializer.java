package com.jtbdevelopment.games.mongo.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jtbdevelopment.core.spring.jackson.AutoRegistrableJsonSerializer;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 12/16/14 Time: 11:42 PM
 */
@Component
public class ObjectIdSerializer extends AutoRegistrableJsonSerializer<ObjectId> {

  @Override
  public void serialize(final ObjectId value, final JsonGenerator jgen,
      final SerializerProvider provider) throws IOException {
    jgen.writeString(value.toHexString());
  }

  @Override
  public Class<ObjectId> handledType() {
    return ObjectId.class;
  }

}
