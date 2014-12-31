package com.jtbdevelopment.games.games

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 12/31/2014
 * Time: 5:07 PM
 *
 * A MultiPlayerGame may still be played by a single player
 * It just has the potential to be played by more than one
 */
@CompileStatic
interface MultiPlayerGame<ID extends Serializable> extends Game<ID> {
    ID getInitiatingPlayer()

    void setInitiatingPlayer(final ID initiatingPlayer)

    //  Order list potentially
    List<Player<ID>> getPlayers()

    void setPlayers(final List<Player<ID>> players)

    Map<ID, PlayerState> getPlayerStates();

    void setPlayerStates(final Map<ID, PlayerState> playerState)

    ZonedDateTime getDeclinedTimestamp()

    void setDeclinedTimestamp(final ZonedDateTime declinedTimestamp)
}