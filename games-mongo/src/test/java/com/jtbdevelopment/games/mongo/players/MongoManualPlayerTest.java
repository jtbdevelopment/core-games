package com.jtbdevelopment.games.mongo.players;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 12/16/14 Time: 7:00 AM
 */
public class MongoManualPlayerTest {

  @Test
  public void testClassAnnotations() {
    assertEquals("player", MongoManualPlayer.class.getAnnotation(Document.class).collection());
  }

  @Test
  public void testSourceDefaults() {
    MongoManualPlayer p = new MongoManualPlayer();

    assertEquals(MongoManualPlayer.MANUAL_SOURCE, p.getSource());
  }

}
