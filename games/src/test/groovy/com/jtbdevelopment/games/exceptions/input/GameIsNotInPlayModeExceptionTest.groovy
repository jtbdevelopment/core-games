package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 1/13/15
 * Time: 6:52 PM
 */
class GameIsNotInPlayModeExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new GameIsNotInPlayModeException().message == 'Game is not open for playing.'
    }
}
