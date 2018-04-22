package com.jtbdevelopment.games.mongo.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.data.annotation.Id;

/**
 * Date: 1/9/15 Time: 10:47 PM
 */
public class AbstractMongoSinglePlayerGameTest {

  @Test
  public void testIdAnnotation() throws NoSuchFieldException {
    assertTrue(
        AbstractMongoSinglePlayerGame.class.getDeclaredField("id").isAnnotationPresent(Id.class));
  }

  @Test
  public void testIdAsString() {
    ObjectId id = new ObjectId();
    ObjectId previous = new ObjectId();
    AGame game = new AGame();
    game.setId(id);
    game.setPreviousId(previous);
    assertSame(id, game.getId());
    assertEquals(id.toHexString(), game.getIdAsString());
    assertEquals(previous.toHexString(), game.getPreviousIdAsString());
  }

  @Test
  public void testIdAsStringNullId() {
    AGame game = new AGame();
    assertNull(game.getId());
    assertNull(game.getIdAsString());
    assertNull(game.getPreviousId());
    assertNull(game.getPreviousIdAsString());
  }

  private static class AGame extends AbstractMongoSinglePlayerGame {

  }
}
