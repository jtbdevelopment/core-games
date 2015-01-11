package com.jtbdevelopment.games.exceptions

/**
 * Date: 1/11/15
 * Time: 12:57 PM
 */
class GameSystemExceptionTest extends GroovyTestCase {
    void testMessage() {
        String m = 'message'
        assert m == new GameSystemException(m).message
    }
}
