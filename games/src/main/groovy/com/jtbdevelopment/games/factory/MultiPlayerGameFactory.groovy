package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/4/2015
 * Time: 9:22 PM
 */
@CompileStatic
interface MultiPlayerGameFactory<IMPL extends MultiPlayerGame, FEATURES> {
    public IMPL createGame(
            final Set<FEATURES> features,
            final List<Player> players,
            final Player initiatingPlayer)

    public IMPL createGame(final IMPL previousGame, final Player initiatingPlayer)
}