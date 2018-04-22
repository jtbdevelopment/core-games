package com.jtbdevelopment.games.mongo.dao;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 3/7/15 Time: 3:06 PM
 */
public class ObjectIDToStringConverterTest {

  private ObjectIDToStringConverter converter = new ObjectIDToStringConverter();

  @Test
  public void testConvertNull() {
    Assert.assertNull(converter.convert(null));
  }

  @Test
  public void testConvert() {
    ObjectId objectId = new ObjectId();
    Assert.assertEquals(objectId.toHexString(), converter.convert(objectId));
  }
}
