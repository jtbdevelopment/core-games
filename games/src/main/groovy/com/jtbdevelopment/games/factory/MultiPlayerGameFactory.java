package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.util.List;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 9:22 PM
 */
public interface MultiPlayerGameFactory<IMPL extends MultiPlayerGame, FEATURES> {

  IMPL createGame(final Set<FEATURES> features, final List<Player> players,
      final Player initiatingPlayer);

  IMPL createGame(final IMPL previousGame, final Player initiatingPlayer);
}
