package com.jtbdevelopment.games

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.AbstractGame

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */
class StringGame extends AbstractGame<String, Object> {
    String id

    @Override
    String getIdAsString() {
        return id
    }

    String previousId

    @Override
    String getPreviousIdAsString() {
        return previousId
    }

    @Override
    List<Player<String>> getAllPlayers() {
        null
    }
}
