package com.jtbdevelopment.games.games

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 1/7/15
 * Time: 6:38 AM
 */
@CompileStatic
abstract class AbstractSinglePlayerGame<ID extends Serializable, FEATURES> extends AbstractGame<ID, FEATURES> implements SinglePlayerGame<ID, ZonedDateTime, FEATURES> {
    Player<ID> player
}
