package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.state.Game;
import org.springframework.core.Ordered;

/**
 * Date: 1/13/15 Time: 7:12 AM
 */
public interface GameInitializer<IMPL extends Game> extends Ordered {

  @SuppressWarnings("unused")
  int EARLY_ORDER = 1;
  int DEFAULT_ORDER = 1000;
  @SuppressWarnings("unused")
  int LATE_ORDER = 10000;

  void initializeGame(final IMPL game);
}
