package com.jtbdevelopment.games.games

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 12/31/2014
 * Time: 5:16 PM
 */
@CompileStatic
interface SinglePlayerGame<ID extends Serializable, TIMESTAMP, FEATURES> extends Game<ID, TIMESTAMP, FEATURES> {
    Player<ID> getPlayer()

    void setPlayer(final Player<ID> player)
}
