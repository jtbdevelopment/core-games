package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;

/**
 * Date: 10/10/2015 Time: 2:54 PM
 */
public interface WebSocketPublicationListener {

  void publishedPlayerUpdate(final Player<?> player, boolean status);

  void publishedGameUpdateToPlayer(final Player<?> player, final Game game, boolean status);
}
