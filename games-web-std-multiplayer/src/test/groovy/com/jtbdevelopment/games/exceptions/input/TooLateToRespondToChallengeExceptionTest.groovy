package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 1/13/15
 * Time: 6:44 PM
 */
class TooLateToRespondToChallengeExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new TooLateToRespondToChallengeException().message == 'This game is no longer in challenge mode.'
    }
}
