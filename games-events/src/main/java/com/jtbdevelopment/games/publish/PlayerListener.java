package com.jtbdevelopment.games.publish;

import com.jtbdevelopment.games.players.Player;
import java.io.Serializable;

/**
 * Date: 2/5/15 Time: 9:45 PM
 */
public interface PlayerListener<ID extends Serializable, P extends Player<ID>> {

  void playerChanged(final P player, final boolean initiatingServer);

  void allPlayersChanged(final boolean initiatingServer);
}
