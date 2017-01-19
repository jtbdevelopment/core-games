package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 2/19/15
 * Time: 7:14 AM
 */
@CompileStatic
interface GameMasker<ID extends Serializable, U extends Game, M extends MaskedGame> {
    M maskGameForPlayer(final U game, final Player<ID> player)
}
