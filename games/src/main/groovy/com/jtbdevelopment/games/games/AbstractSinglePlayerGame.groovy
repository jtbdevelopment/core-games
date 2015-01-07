package com.jtbdevelopment.games.games

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 1/7/15
 * Time: 6:38 AM
 */
@CompileStatic
abstract class AbstractSinglePlayerGame<ID extends Serializable> extends AbstractGame<ID> implements SinglePlayerGame<ID> {
    Player<ID> player
}
