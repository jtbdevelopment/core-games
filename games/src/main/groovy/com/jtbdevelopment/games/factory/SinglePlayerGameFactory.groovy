package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.SinglePlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/4/2015
 * Time: 9:37 PM
 */
@CompileStatic
interface SinglePlayerGameFactory<IMPL extends SinglePlayerGame, FEATURES> {
    public IMPL createGame(
            final Set<FEATURES> features,
            final Player player)

    public IMPL createGame(final IMPL previousGame)
}