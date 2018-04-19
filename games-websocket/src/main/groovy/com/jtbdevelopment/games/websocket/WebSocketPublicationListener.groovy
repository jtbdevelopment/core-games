package com.jtbdevelopment.games.websocket

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 10/10/2015
 * Time: 2:54 PM
 */
@CompileStatic
interface WebSocketPublicationListener {
    void publishedPlayerUpdate(final Player<?> player, boolean status)

    void publishedGameUpdateToPlayer(final Player<?> player, final Game game, boolean status)
}