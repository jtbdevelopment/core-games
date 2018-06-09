package com.jtbdevelopment.games.state.masking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Date: 2/19/15 Time: 7:05 AM
 */
public class AbstractMaskedMultiPlayerGameTest {

  @Test
  public void testInitializes() {
    String ID = "ANID";
    TestMaskedGame game = new TestMaskedGame();
    game.setId(ID);

    assertEquals(ID, game.getIdAsString());
    assertTrue(game.getFeatures().isEmpty());
    assertTrue(game.getPlayerImages().isEmpty());
    assertTrue(game.getPlayerProfiles().isEmpty());
    assertTrue(game.getPlayerStates().isEmpty());
    assertTrue(game.getPlayers().isEmpty());
    assertNull(game.getMaskedForPlayerID());
    assertNull(game.getMaskedForPlayerMD5());
    assertNull(game.getInitiatingPlayer());
    assertNull(game.getCompletedTimestamp());
    assertNull(game.getDeclinedTimestamp());
    assertNull(game.getCreated());
    assertNull(game.getLastUpdate());
    assertNull(game.getVersion());
    assertEquals(game.getId(), ID);
  }

  private static class TestMaskedGame extends AbstractMaskedMultiPlayerGame {

  }
}
