package com.jtbdevelopment.games.games

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 12/31/2014
 * Time: 5:30 PM
 */
@CompileStatic
abstract class AbstractMultiPlayerGame<ID extends Serializable> extends AbstractGame<ID> implements MultiPlayerGame<ID> {
    ID initiatingPlayer
    List<Player<ID>> players = []
    Map<ID, PlayerState> playerStates = [:]

    ZonedDateTime declinedTimestamp
}
