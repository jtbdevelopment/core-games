package com.jtbdevelopment.games.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/11/15 Time: 12:57 PM
 */
public class GameInputExceptionTest {

  @Test
  public void testMessage() {
    String m = "message";
    Assert.assertEquals(m, new GameInputException(m).getMessage());
  }

}
