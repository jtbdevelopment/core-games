package com.jtbdevelopment.games.exceptions.input;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Date: 1/13/15 Time: 6:52 PM
 */
public class GameIsNotInPlayModeExceptionTest {

  @Test
  public void testMessage() {
    assertEquals("Game is not open for playing.", new GameIsNotInPlayModeException().getMessage());
  }

}
