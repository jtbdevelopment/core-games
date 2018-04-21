package com.jtbdevelopment.games.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.games.StringGame;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 * Date: 1/7/15 Time: 6:40 AM
 */
public class AbstractGameTest {

  @Test
  public void testVersionAnnotations() throws NoSuchFieldException {
    Field m = AbstractGame.class.getDeclaredField("version");
    Assert.assertNotNull(m);
    Assert.assertNotNull(m.getAnnotation(Version.class));
  }

  @Test
  public void testCreatedAnnotations() throws NoSuchFieldException {
    Field m = AbstractGame.class.getDeclaredField("created");
    Assert.assertNotNull(m);
    Assert.assertNotNull(m.getAnnotation(CreatedDate.class));
  }

  @Test
  public void testLastUpdateTimestampAnnotations() throws NoSuchFieldException {
    Field m = AbstractGame.class.getDeclaredField("lastUpdate");
    Assert.assertNotNull(m);
    Assert.assertNotNull(m.getAnnotation(LastModifiedDate.class));
  }

  @Test
  public void testEquals() {
    StringGame game = new StringGame();
    game.setId("TEST");
    StringGame game2 = new StringGame();
    game2.setId(game.getId());
    assertEquals(game, game2);
    DerivedStringGame game3 = new DerivedStringGame();
    game3.setId(game.getId());
    game3.setAnotherField("X");
    assertEquals(game, game3);
    ComplexStringIdGame game4 = new ComplexStringIdGame();
    game4.setId(game.getId());
    assertEquals(game, game4);
    StringGame game5 = new StringGame();

    game5.setId(game.getId().toLowerCase());
    Assert.assertNotEquals(game, game5);
    Assert.assertNotEquals(game, game.getId());
    Assert.assertNotEquals(game, null);
  }

  @Test
  public void testHashCodeNullId() {
    assertEquals(new StringGame().hashCode(), 0);
  }

  @Test
  public void testHashCodeUsesIdAsString() {
    ComplexStringIdGame game = new ComplexStringIdGame();
    game.setId("TEST");
    assertEquals(game.hashCode(), "TESTXYZ".hashCode());
  }

  @Test
  public void testConstructor() {
    StringGame game = new StringGame();
    assertNull(game.getId());
    assertNull(game.getLastUpdate());
    assertNull(game.getCreated());
    assertNull(game.getCompletedTimestamp());
    assertTrue(game.getFeatureData().isEmpty());
    assertTrue(game.getFeatures().isEmpty());
    assertEquals(0, game.getRound());
    assertNull(game.getPreviousId());
    assertEquals(GamePhase.Setup, game.getGamePhase());
  }

  private static class DerivedStringGame extends StringGame {

    private String anotherField;

    public String getAnotherField() {
      return anotherField;
    }

    public void setAnotherField(String anotherField) {
      this.anotherField = anotherField;
    }
  }

  private static class ComplexStringIdGame extends StringGame {

    @Override
    public String getIdAsString() {
      return super.getIdAsString() + "XYZ";
    }

  }
}
