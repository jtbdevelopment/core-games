package com.jtbdevelopment.games.exceptions.input

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 1/13/15
 * Time: 6:53 PM
 */
class GameIsNotPossibleToQuitNowException extends GameInputException {
    static final String ERROR = 'Game is not available to quit anymore.'

    GameIsNotPossibleToQuitNowException() {
        super(ERROR);
    }
}
