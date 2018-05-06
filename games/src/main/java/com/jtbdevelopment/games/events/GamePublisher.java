package com.jtbdevelopment.games.events;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;

/**
 * Date: 12/8/14 Time: 6:40 PM
 */
public interface GamePublisher<IMPL extends Game, P extends Player> {

  IMPL publish(final IMPL game, final P initiatingPlayer);

  IMPL publish(final IMPL game, final P initiatingPlayer, boolean initiatingServer);
}
