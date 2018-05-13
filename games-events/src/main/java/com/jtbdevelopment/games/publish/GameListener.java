package com.jtbdevelopment.games.publish;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;

/**
 * Date: 1/13/15 Time: 7:56 AM
 */
public interface GameListener<T extends Game, P extends Player> {

  void gameChanged(
      final T game,
      final P initiatingPlayer,
      final boolean initiatingServer);
}
