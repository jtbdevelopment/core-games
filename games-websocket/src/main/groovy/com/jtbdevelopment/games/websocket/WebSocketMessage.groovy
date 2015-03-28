package com.jtbdevelopment.games.websocket

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.masked.MaskedMultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 12/8/14
 * Time: 6:59 AM
 */
@CompileStatic
class WebSocketMessage {
    public enum MessageType {
        Heartbeat,      //  Check message
        Game,           //  Check game
        Player,         //  Check player
        Alert,          //  Check message
    }

    MessageType messageType
    MaskedMultiPlayerGame game
    Player player
    String message
}
