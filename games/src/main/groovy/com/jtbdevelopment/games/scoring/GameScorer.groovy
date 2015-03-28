package com.jtbdevelopment.games.scoring

import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 3/28/15
 * Time: 2:03 PM
 */
@CompileStatic
interface GameScorer<IMPL extends Game> {
    public IMPL scoreGame(final IMPL game)
}
