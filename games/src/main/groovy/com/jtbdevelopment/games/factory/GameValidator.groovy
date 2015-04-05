package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 1/13/15
 * Time: 7:12 AM
 */
@CompileStatic
interface GameValidator<IMPL extends Game> {
    boolean validateGame(final IMPL game)

    String errorMessage()
}