package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import java.io.Serializable;
import java.util.Optional;

/**
 * Date: 4/8/2015 Time: 8:22 PM
 *
 * You will most likely need to override the evaluate setup and playing functions
 */
public abstract class AbstractMPGamePhaseTransitionEngine<
    ID extends Serializable,
    FEATURURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURURES>>
    extends AbstractGamePhaseTransitionEngine<ID, FEATURURES, IMPL> {

  @SuppressWarnings("WeakerAccess")
  protected AbstractMPGamePhaseTransitionEngine(final GameScorer<IMPL> gameScorer) {
    super(gameScorer);
  }

  @Override
  protected IMPL evaluateRoundOverPhase(final IMPL game) {
    if (game.getRematchTimestamp() != null) {
      return changeStateAndReevaluate(GamePhase.NextRoundStarted, game);
    }
    return super.evaluateRoundOverPhase(game);
  }

  @Override
  protected IMPL evaluateChallengedPhase(final IMPL game) {
    Optional rejected = game.getPlayerStates().values()
        .stream()
        .filter(PlayerState.Rejected::equals)
        .findAny();
    if (rejected.isPresent()) {
      return changeStateAndReevaluate(GamePhase.Declined, game);
    } else {
      Optional pending = game.getPlayerStates().values()
          .stream()
          .filter(PlayerState.Pending::equals)
          .findAny();

      if (!pending.isPresent()) {
        return changeStateAndReevaluate(GamePhase.Setup, game);
      }
    }

    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL changeStateAndReevaluate(final GamePhase transitionTo, final IMPL game) {
    game.setGamePhase(transitionTo);
    return evaluateGame(game);
  }
}
