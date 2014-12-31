package com.jtbdevelopment.games.exceptions

import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 1:13 PM
 */
@CompileStatic
class GameInputException extends GameException {
    GameInputException(final String s) {
        super(s)
    }
}