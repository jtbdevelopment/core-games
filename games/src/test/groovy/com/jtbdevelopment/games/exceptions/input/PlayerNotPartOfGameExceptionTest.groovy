package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 1/13/15
 * Time: 7:03 PM
 */
class PlayerNotPartOfGameExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert 'Player trying to act on a game they are not part of.' == new PlayerNotPartOfGameException().message
    }
}
