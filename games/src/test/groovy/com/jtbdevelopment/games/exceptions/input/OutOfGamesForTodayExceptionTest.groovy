package com.jtbdevelopment.games.exceptions.input

/**
 * Date: 2/10/15
 * Time: 6:40 AM
 */
class OutOfGamesForTodayExceptionTest extends GroovyTestCase {
    OutOfGamesForTodayException exception = new OutOfGamesForTodayException()

    void testMessage() {
        assert exception.message == 'No more games available for today.  Purchase more if you want to continue playing or wait for tomorrow.'
    }
}
