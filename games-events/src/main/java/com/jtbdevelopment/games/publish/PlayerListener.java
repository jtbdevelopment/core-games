package com.jtbdevelopment.games.publish;

import com.jtbdevelopment.games.players.Player;

/**
 * Date: 2/5/15 Time: 9:45 PM
 */
public interface PlayerListener {

  void playerChanged(final Player player, final boolean initiatingServer);

  void allPlayersChanged(final boolean initiatingServer);
}
