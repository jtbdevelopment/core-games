package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 4/8/2015
 * Time: 10:01 PM
 */
class GameIsNotAvailableToRematchExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new GameIsNotAvailableToRematchException().message == 'Game is not available for rematching.'
    }
}
