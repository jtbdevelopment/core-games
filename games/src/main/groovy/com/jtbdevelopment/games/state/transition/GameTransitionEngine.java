package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.state.Game;

/**
 * Date: 3/28/15 Time: 2:27 PM
 */
public interface GameTransitionEngine<IMPL extends Game> {

  IMPL evaluateGame(final IMPL game);
}
