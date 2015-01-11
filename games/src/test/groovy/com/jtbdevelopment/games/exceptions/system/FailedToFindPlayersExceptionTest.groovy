package com.jtbdevelopment.games.exceptions.system

/**
 * Date: 1/11/15
 * Time: 1:00 PM
 */
class FailedToFindPlayersExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert new FailedToFindPlayersException().message == "Not all players in this game are valid anymore."
    }
}
