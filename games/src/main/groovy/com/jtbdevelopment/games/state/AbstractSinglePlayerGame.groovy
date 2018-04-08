package com.jtbdevelopment.games.state

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

import java.time.Instant

/**
 * Date: 1/7/15
 * Time: 6:38 AM
 */
@CompileStatic
abstract class AbstractSinglePlayerGame<ID extends Serializable, FEATURES> extends AbstractGame<ID, FEATURES> implements SinglePlayerGame<ID, Instant, FEATURES> {
    Player<ID> player

    List<Player<ID>> getAllPlayers() {
        return [player]
    }
}
