package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 9:22 PM
 */
public interface MultiPlayerGameFactory<ID extends Serializable, FEATURES, IMPL extends MultiPlayerGame<ID, ?, FEATURES>> {

  IMPL createGame(
      final Set<FEATURES> features,
      final List<Player<ID>> players,
      final Player<ID> initiatingPlayer);

  IMPL createGame(final IMPL previousGame, final Player<ID> initiatingPlayer);
}
