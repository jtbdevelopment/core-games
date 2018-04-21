package com.jtbdevelopment.games.exceptions.system;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/13/15 Time: 6:33 PM
 */
public class FailedToFindGameExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("Was not able to load game.", new FailedToFindGameException().getMessage());
    }

}
