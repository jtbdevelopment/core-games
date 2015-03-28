package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 1/13/15
 * Time: 7:56 AM
 */
@CompileStatic
interface GameListener {
    void gameChanged(final MultiPlayerGame game, final Player initiatingPlayer, final boolean initiatingServer)
}