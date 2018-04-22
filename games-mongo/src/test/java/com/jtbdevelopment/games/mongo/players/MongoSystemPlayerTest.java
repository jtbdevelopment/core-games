package com.jtbdevelopment.games.mongo.players;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 1/8/15 Time: 10:11 PM
 */
public class MongoSystemPlayerTest {

  @Test
  public void testClassAnnotations() {
    assertEquals("player", MongoSystemPlayer.class.getAnnotation(Document.class).collection());
  }

  @Test
  public void testSourceDefaults() {
    MongoSystemPlayer p = new MongoSystemPlayer();

    assertEquals(MongoSystemPlayer.SYSTEM_SOURCE, p.getSource());
  }

}
