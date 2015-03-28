package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 3/28/15
 * Time: 2:27 PM
 */
@CompileStatic
interface GameTransitionEngine<IMPL extends Game> {
    IMPL evaluateGame(final IMPL game)
}