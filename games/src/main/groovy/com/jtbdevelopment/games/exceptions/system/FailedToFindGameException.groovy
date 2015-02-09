package com.jtbdevelopment.games.exceptions.system

import com.jtbdevelopment.games.exceptions.GameSystemException

/**
 * Date: 1/13/15
 * Time: 6:32 PM
 */
class FailedToFindGameException extends GameSystemException {
    public final static String VALID_GAME = 'Was not able to load game.'

    public FailedToFindGameException() {
        super(VALID_GAME)
    }
}