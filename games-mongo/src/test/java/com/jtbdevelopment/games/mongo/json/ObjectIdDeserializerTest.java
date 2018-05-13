package com.jtbdevelopment.games.mongo.json;

import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 12/22/14 Time: 12:06 PM
 */
public class ObjectIdDeserializerTest {

  private ObjectIdDeserializer objectIdDeserializer = new ObjectIdDeserializer();

  @Test
  public void testDeserialize() throws IOException {
    ObjectId start = new ObjectId();
    JsonParser parser = Mockito.mock(JsonParser.class);
    Mockito.when(parser.getValueAsString()).thenReturn(start.toHexString());

    Assert.assertEquals(start, objectIdDeserializer.deserialize(parser, null));
  }

  @Test
  public void testGetRegisterForClass() {
    Assert.assertEquals(ObjectId.class, (objectIdDeserializer.handledType()));
  }
}
