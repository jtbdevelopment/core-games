package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 1/13/15
 * Time: 7:09 PM
 */
class PlayerOutOfTurnExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert 'Player is playing out of turn.' == new PlayerOutOfTurnException().message
    }
}
