package com.jtbdevelopment.games.exceptions.system

import com.jtbdevelopment.games.exceptions.GameSystemException
import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 1:15 PM
 */
@CompileStatic
class FailedToFindPlayersException extends GameSystemException {
    public final static String VALID_PLAYERS = "Not all players in this game are valid anymore."

    public FailedToFindPlayersException() {
        super(VALID_PLAYERS);
    }
}
