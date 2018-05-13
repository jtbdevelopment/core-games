package com.jtbdevelopment.games.exceptions.input;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Date: 1/13/15 Time: 6:44 PM
 */
public class TooLateToRespondToChallengeExceptionTest {

    @Test
    public void testMessage() {
        assertEquals("This game is no longer in challenge mode.",
            new TooLateToRespondToChallengeException().getMessage());
    }

}
