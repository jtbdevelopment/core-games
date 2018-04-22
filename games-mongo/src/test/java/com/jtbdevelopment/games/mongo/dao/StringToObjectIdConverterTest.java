package com.jtbdevelopment.games.mongo.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.bson.types.ObjectId;
import org.junit.Test;

/**
 * Date: 3/7/15 Time: 3:03 PM
 */
public class StringToObjectIdConverterTest {

  private StringToObjectIdConverter converter = new StringToObjectIdConverter();

  @Test
  public void testConvertNull() {
    assertNull(converter.convert(null));
  }

  @Test
  public void testConvert() {
    ObjectId objectId = new ObjectId();
    assertEquals(objectId, converter.convert(objectId.toHexString()));
  }

  @Test(expected = Exception.class)
  public void testConvertFail() {
    converter.convert("random string");
  }
}
