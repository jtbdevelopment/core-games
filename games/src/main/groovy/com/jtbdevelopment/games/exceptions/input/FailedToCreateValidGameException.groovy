package com.jtbdevelopment.games.exceptions.input

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 1/13/15
 * Time: 6:48 PM
 */
class FailedToCreateValidGameException extends GameInputException {
    static final String BASE_ERROR = 'System failed to create a valid game.  '

    FailedToCreateValidGameException(final String s) {
        super(BASE_ERROR + s)
    }
}
