package com.jtbdevelopment.games.exceptions.input

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 1/13/15
 * Time: 6:48 PM
 */
class GameIsNotInPlayModeException extends GameInputException {
    static final String ERROR = 'Game is not open for playing.'

    GameIsNotInPlayModeException() {
        super(ERROR)
    }
}
