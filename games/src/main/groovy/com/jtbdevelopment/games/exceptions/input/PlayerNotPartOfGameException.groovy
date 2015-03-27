package com.jtbdevelopment.games.exceptions.input

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 1/13/15
 * Time: 6:59 PM
 */
class PlayerNotPartOfGameException extends GameInputException {
    static final String MESSAGE = 'Player trying to act on a game they are not part of.'

    PlayerNotPartOfGameException() {
        super(MESSAGE)
    }
}
