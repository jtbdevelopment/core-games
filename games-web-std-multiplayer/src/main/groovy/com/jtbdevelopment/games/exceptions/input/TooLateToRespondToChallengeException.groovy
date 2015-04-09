package com.jtbdevelopment.games.exceptions.input

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 1/13/15
 * Time: 6:43 PM
 */
class TooLateToRespondToChallengeException extends GameInputException {

    static final String ERROR = 'This game is no longer in challenge mode.'

    TooLateToRespondToChallengeException() {
        super(ERROR)
    }

}
