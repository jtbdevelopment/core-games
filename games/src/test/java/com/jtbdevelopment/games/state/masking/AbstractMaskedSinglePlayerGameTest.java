package com.jtbdevelopment.games.state.masking;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 2/19/15 Time: 7:05 AM
 */
public class AbstractMaskedSinglePlayerGameTest {

  @Test
  public void testInitializes() {
    String ID = "ANID";
    TestMaskedGame game = new TestMaskedGame();

    game.setId(ID);
    Assert.assertEquals(ID, game.getIdAsString());
    Assert.assertTrue(game.getFeatures().isEmpty());
    Assert.assertTrue(game.getFeatureData().isEmpty());
    Assert.assertTrue(game.getPlayerImages().isEmpty());
    Assert.assertTrue(game.getPlayerProfiles().isEmpty());
    Assert.assertTrue(game.getPlayers().isEmpty());
    Assert.assertNull(game.getCompletedTimestamp());
    Assert.assertNull(game.getCreated());
    Assert.assertNull(game.getLastUpdate());
    Assert.assertNull(game.getVersion());
    Assert.assertEquals(game.getId(), ID);
  }

  private static class TestMaskedGame extends AbstractMaskedSinglePlayerGame {

  }
}
