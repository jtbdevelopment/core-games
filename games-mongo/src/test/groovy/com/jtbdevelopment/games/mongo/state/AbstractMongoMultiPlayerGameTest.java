package com.jtbdevelopment.games.mongo.state;

import groovy.util.GroovyTestCase;
import junit.framework.TestCase;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.data.annotation.Id;

/**
 * Date: 1/9/15 Time: 10:41 PM
 */
public class AbstractMongoMultiPlayerGameTest extends GroovyTestCase {

  @Test
  public void testIdAnnotation() throws NoSuchFieldException {
    assert AbstractMongoMultiPlayerGame.class.getDeclaredField("id").isAnnotationPresent(Id.class);
  }

  @Test
  public void testIdAsString() {
    ObjectId id = new ObjectId();
    ObjectId previous = new ObjectId();
    AGame game = new AGame();
    game.setId(id);
    game.setPreviousId(previous);
    TestCase.assertSame(id, game.getId());
    GroovyTestCase.assertEquals(id.toHexString(), game.getIdAsString());
    TestCase.assertSame(previous, game.getPreviousId());
    GroovyTestCase.assertEquals(previous.toHexString(), game.getPreviousIdAsString());
  }

  @Test
  public void testIdAsStringNullId() {
    AGame game = new AGame();
    TestCase.assertNull(game.getId());
    TestCase.assertNull(game.getIdAsString());
    TestCase.assertNull(game.getPreviousId());
    TestCase.assertNull(game.getPreviousIdAsString());
  }

  private static class AGame extends AbstractMongoMultiPlayerGame {

  }
}
