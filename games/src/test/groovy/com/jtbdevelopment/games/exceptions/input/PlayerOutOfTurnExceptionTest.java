package com.jtbdevelopment.games.exceptions.input;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/13/15 Time: 7:09 PM
 */
public class PlayerOutOfTurnExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("Player is playing out of turn.",
        new PlayerOutOfTurnException().getMessage());
  }

}
