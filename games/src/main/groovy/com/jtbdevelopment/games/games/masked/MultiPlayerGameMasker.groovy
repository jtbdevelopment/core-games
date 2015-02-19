package com.jtbdevelopment.games.games.masked

import com.jtbdevelopment.games.games.MultiPlayerGame
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 2/19/15
 * Time: 7:14 AM
 */
@CompileStatic
interface MultiPlayerGameMasker<ID extends Serializable, U extends MultiPlayerGame, M extends MaskedMultiPlayerGame> {
    M maskGameForPlayer(final U game, final Player<ID> player)
}
