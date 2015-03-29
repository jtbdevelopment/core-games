package com.jtbdevelopment.games.exceptions.input

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 2/8/15
 * Time: 7:55 PM
 */
class OutOfGamesForTodayException extends GameInputException {
    private static
    final String MESSAGE = 'No more games available for today.  Purchase more if you want to continue playing or wait for tomorrow.'

    OutOfGamesForTodayException() {
        super(MESSAGE)
    }
}
