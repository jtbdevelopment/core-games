package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.state.Game;

/**
 * Date: 1/13/15 Time: 7:12 AM
 */
public interface GameValidator<IMPL extends Game> {

  boolean validateGame(final IMPL game);

  String errorMessage();
}
