package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 1/13/15
 * Time: 6:57 PM
 */
class GameIsNotPossibleToQuitNowExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new GameIsNotPossibleToQuitNowException().message == 'Game is not available to quit anymore.'
    }
}
