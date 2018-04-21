package com.jtbdevelopment.games.exceptions.input;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 2/10/15 Time: 6:40 AM
 */
public class OutOfGamesForTodayExceptionTest {

  private OutOfGamesForTodayException exception = new OutOfGamesForTodayException();

  @Test
  public void testMessage() {
    Assert.assertEquals(
        "No more games available for today.  Purchase more if you want to continue playing or wait for tomorrow.",
        exception.getMessage());
  }
}
