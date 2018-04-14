package com.jtbdevelopment.games.scoring;

import com.jtbdevelopment.games.state.Game;

/**
 * Date: 3/28/15 Time: 2:03 PM
 */
public interface GameScorer<IMPL extends Game> {

  IMPL scoreGame(final IMPL game);
}
