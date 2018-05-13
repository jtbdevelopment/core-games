package com.jtbdevelopment.games.exceptions.input;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 4/4/2015 Time: 8:54 PM
 */
public class FailedToCreateValidGameExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("System failed to create a valid game.  Too bad.",
        new FailedToCreateValidGameException("Too bad.").getMessage());
  }

}
