package com.jtbdevelopment.games.exceptions.system;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/11/15 Time: 1:00 PM
 */
public class FailedToFindPlayersExceptionTest {

  @Test
  public void testMessage() {
    Assert.assertEquals("Not all players in this game are valid anymore.",
        new FailedToFindPlayersException().getMessage());
    }

}
