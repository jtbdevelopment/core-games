package com.jtbdevelopment.games.exceptions

import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 1:13 PM
 */
@CompileStatic
class GameSystemException extends GameException {
    GameSystemException(final String s) {
        super(s)
    }
}