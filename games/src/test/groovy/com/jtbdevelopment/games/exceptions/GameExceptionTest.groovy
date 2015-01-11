package com.jtbdevelopment.games.exceptions

/**
 * Date: 1/11/15
 * Time: 12:59 PM
 */
class GameExceptionTest extends GroovyTestCase {
    void testMessage() {
        String m = 'message'
        assert m == new GameException(m).message
    }
}
