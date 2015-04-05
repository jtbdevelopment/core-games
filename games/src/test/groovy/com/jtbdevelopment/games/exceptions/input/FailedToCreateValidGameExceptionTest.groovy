package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 4/4/2015
 * Time: 8:54 PM
 */
class FailedToCreateValidGameExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new FailedToCreateValidGameException('Too bad.').message == 'System failed to create a valid game.  Too bad.'
    }
}
