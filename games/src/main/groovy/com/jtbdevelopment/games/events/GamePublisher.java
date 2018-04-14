package com.jtbdevelopment.games.events;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;

/**
 * Date: 12/8/14 Time: 6:40 PM
 */
public interface GamePublisher<IMPL extends Game> {

  IMPL publish(final IMPL game, final Player initiatingPlayer);

  IMPL publish(final IMPL game, final Player initiatingPlayer, boolean initiatingServer);
}
