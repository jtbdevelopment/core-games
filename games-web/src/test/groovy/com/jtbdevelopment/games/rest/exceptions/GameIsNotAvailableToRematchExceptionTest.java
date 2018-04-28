package com.jtbdevelopment.games.rest.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 4/8/2015 Time: 10:01 PM
 */
public class GameIsNotAvailableToRematchExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("Game is not available for rematching.",
        new GameIsNotAvailableToRematchException().getMessage());
  }

}
