package com.jtbdevelopment.games.mongo.json;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 12/22/14 Time: 12:04 PM
 */
public class ObjectIdSerializerTest {

  private ObjectIdSerializer serializer = new ObjectIdSerializer();

  @Test
  public void testSerializesHexString() throws IOException {
    ObjectId id = new ObjectId();
    JsonGenerator gen = Mockito.mock(JsonGenerator.class);
    serializer.serialize(id, gen, null);
    Mockito.verify(gen).writeString(id.toHexString());
  }

  @Test
  public void testGetRegisterForClass() {
    Assert.assertEquals(ObjectId.class, serializer.handledType());
  }
}
