package com.jtbdevelopment.games.exceptions.system

/**
 * Date: 1/13/15
 * Time: 6:33 PM
 */
class FailedToFindGameExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new FailedToFindGameException().message == 'Was not able to load game.'
    }
}
