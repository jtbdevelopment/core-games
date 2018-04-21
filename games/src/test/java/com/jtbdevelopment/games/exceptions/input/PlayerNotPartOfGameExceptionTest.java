package com.jtbdevelopment.games.exceptions.input;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/13/15 Time: 7:03 PM
 */
public class PlayerNotPartOfGameExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("Player trying to act on a game they are not part of.",
        new PlayerNotPartOfGameException().getMessage());
  }

}
