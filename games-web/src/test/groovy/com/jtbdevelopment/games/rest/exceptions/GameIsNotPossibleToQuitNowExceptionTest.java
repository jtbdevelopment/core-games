package com.jtbdevelopment.games.rest.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/13/15 Time: 6:57 PM
 */
public class GameIsNotPossibleToQuitNowExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("Game is not available to quit anymore.",
        new GameIsNotPossibleToQuitNowException().getMessage());
  }

}
