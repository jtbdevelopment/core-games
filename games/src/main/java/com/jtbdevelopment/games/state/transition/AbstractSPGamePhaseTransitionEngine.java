package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import java.io.Serializable;

/**
 * Date: 4/8/2015 Time: 8:22 PM
 *
 * You will most likely need to override the evaluate setup and playing functions
 */
public abstract class AbstractSPGamePhaseTransitionEngine<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractSinglePlayerGame<ID, FEATURES>>
    extends AbstractGamePhaseTransitionEngine<ID, FEATURES, IMPL> {

  @SuppressWarnings("WeakerAccess")
  protected AbstractSPGamePhaseTransitionEngine(
      final GameScorer<IMPL> gameScorer) {
    super(gameScorer);
  }

}
