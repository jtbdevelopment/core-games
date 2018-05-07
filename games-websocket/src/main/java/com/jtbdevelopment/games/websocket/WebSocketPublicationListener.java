package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.io.Serializable;

/**
 * Date: 10/10/2015 Time: 2:54 PM
 */
public interface WebSocketPublicationListener<
    ID extends Serializable,
    TIMESTAMP,
    FEATURES,
    IMPL extends MultiPlayerGame<ID, TIMESTAMP, FEATURES>,
    P extends Player<ID>> {

  void publishedPlayerUpdate(final P player, boolean status);

  void publishedGameUpdateToPlayer(final P player, final IMPL game, boolean status);
}
