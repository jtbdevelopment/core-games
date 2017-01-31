package com.jtbdevelopment.games.state

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 12/31/2014
 * Time: 5:30 PM
 */
@CompileStatic
abstract class AbstractMultiPlayerGame<ID extends Serializable, FEATURES> extends AbstractGame<ID, FEATURES> implements MultiPlayerGame<ID, ZonedDateTime, FEATURES> {
    ID initiatingPlayer
    List<Player<ID>> players = []
    Map<ID, PlayerState> playerStates = [:]

    ZonedDateTime declinedTimestamp
    ZonedDateTime rematchTimestamp

    @Override
    List<Player<ID>> getAllPlayers() {
        return players
    }
}
