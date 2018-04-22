package com.jtbdevelopment.games.mongo.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributesFactory;
import com.jtbdevelopment.games.players.Player;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.annotation.Transient;

/**
 * Date: 1/8/15 Time: 10:10 PM
 */
public class MongoPlayerFactoryTest {

  private MongoPlayerFactory factory = new MongoPlayerFactory();

  @Test
  public void testNewPlayer() {
    Player<ObjectId> player = factory.newPlayer();
    assertTrue(player instanceof MongoPlayer);
    Assert.assertNull(player.getGameSpecificPlayerAttributes());
  }

  @Test
  public void testNewManualPlayer() {
    Player<ObjectId> player = factory.newManualPlayer();
    assertTrue(player instanceof MongoManualPlayer);
    Assert.assertNull(player.getGameSpecificPlayerAttributes());
  }

  @Test
  public void testNewSystemPlayer() {
    Player<ObjectId> player = factory.newSystemPlayer();
    assertTrue(player instanceof MongoSystemPlayer);
    Assert.assertNull(player.getGameSpecificPlayerAttributes());
  }

  @Test
  public void testNewPlayerWithGameAttributes() {
    factory.gameSpecificPlayerAttributesFactory = new GameAttributesFactory();
    Player<ObjectId> player = factory.newPlayer();
    assertTrue(player instanceof MongoPlayer);
    assertNotNull(player.getGameSpecificPlayerAttributes());
    assertSame(player, player.getGameSpecificPlayerAttributes().getPlayer());
    assertEquals(1,
        ((GameAttributes) player.getGameSpecificPlayerAttributes()).getMagicValue());
  }

  @Test
  public void testNewManualPlayerWithGameAttributes() {
    factory.gameSpecificPlayerAttributesFactory = new GameAttributesFactory();
    Player<ObjectId> player = factory.newManualPlayer();
    assertTrue(player instanceof MongoManualPlayer);
    assertNotNull(player.getGameSpecificPlayerAttributes());
    assertSame(player, player.getGameSpecificPlayerAttributes().getPlayer());
    assertEquals(2,
        ((GameAttributes) player.getGameSpecificPlayerAttributes()).getMagicValue());
  }

  @Test
  public void testNewSystemPlayerWithGameAttributes() {
    factory.gameSpecificPlayerAttributesFactory = new GameAttributesFactory();
    Player<ObjectId> player = factory.newSystemPlayer();
    assertTrue(player instanceof MongoSystemPlayer);
    assertNotNull(player.getGameSpecificPlayerAttributes());
    assertSame(player, player.getGameSpecificPlayerAttributes().getPlayer());
    assertEquals(3,
        ((GameAttributes) player.getGameSpecificPlayerAttributes()).getMagicValue());
  }

  private static class GameAttributes implements GameSpecificPlayerAttributes {

    private int magicValue;
    @Transient
    private Player player;

    public int getMagicValue() {
      return magicValue;
    }

    public void setMagicValue(int magicValue) {
      this.magicValue = magicValue;
    }

    public Player getPlayer() {
      return player;
    }

    public void setPlayer(Player player) {
      this.player = player;
    }
  }

  private static class GameAttributesFactory implements GameSpecificPlayerAttributesFactory {

    @Override
    public GameSpecificPlayerAttributes newPlayerAttributes() {
      GameAttributes attributes = new GameAttributes();

      attributes.setMagicValue(1);
      return attributes;
    }

    @Override
    public GameSpecificPlayerAttributes newManualPlayerAttributes() {
      GameAttributes attributes = new GameAttributes();

      attributes.setMagicValue(2);
      return attributes;
    }

    @Override
    public GameSpecificPlayerAttributes newSystemPlayerAttributes() {
      GameAttributes attributes = new GameAttributes();

      attributes.setMagicValue(3);
      return attributes;
    }

  }
}
