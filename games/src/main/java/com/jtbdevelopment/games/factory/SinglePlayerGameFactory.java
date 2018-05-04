package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.io.Serializable;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 9:37 PM
 */
public interface SinglePlayerGameFactory<ID extends Serializable, FEATURES, IMPL extends SinglePlayerGame<ID, ?, FEATURES>> {

  IMPL createGame(final Set<FEATURES> features, final Player<ID> player);

  IMPL createGame(final IMPL previousGame);
}
