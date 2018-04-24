package com.jtbdevelopment.games.player.tracking;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 4/9/15 Time: 9:26 AM
 */
public class AbstractPlayerGameTrackingAttributesTest {

  private AbstractPlayerGameTrackingAttributes attributes = new AbstractPlayerGameTrackingAttributes() {
    private int maxDailyFreeGames;

    public int getMaxDailyFreeGames() {
      return maxDailyFreeGames;
    }

    public void setMaxDailyFreeGames(int maxDailyFreeGames) {
      this.maxDailyFreeGames = maxDailyFreeGames;
    }
  };

  @Test
  public void testInitializesToZero() {
    Assert.assertEquals(0, attributes.getAvailablePurchasedGames());
    Assert.assertEquals(0, attributes.getFreeGamesUsedToday());
  }
}
