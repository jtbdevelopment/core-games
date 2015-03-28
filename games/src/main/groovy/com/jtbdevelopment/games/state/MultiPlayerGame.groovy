package com.jtbdevelopment.games.state

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 12/31/2014
 * Time: 5:07 PM
 *
 * A MultiPlayerGame may still be played by a single player
 * It just has the potential to be played by more than one
 */
@CompileStatic
interface MultiPlayerGame<ID extends Serializable, TIMESTAMP, FEATURES> extends Game<ID, TIMESTAMP, FEATURES> {
    ID getInitiatingPlayer()

    void setInitiatingPlayer(final ID initiatingPlayer)

    //  Order list potentially
    List<Player<ID>> getPlayers()

    void setPlayers(final List<Player<ID>> players)

    Map<ID, PlayerState> getPlayerStates();

    void setPlayerStates(final Map<ID, PlayerState> playerState)

    TIMESTAMP getDeclinedTimestamp()

    void setDeclinedTimestamp(final TIMESTAMP declinedTimestamp)
}