package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import java.io.Serializable;

/**
 * Date: 5/4/18 Time: 8:29 PM
 */
public class AbstractGamePhaseTransitionEngine<ID
    extends Serializable, IMPL extends Game<ID, ?, ?>>
    implements GameTransitionEngine<IMPL> {

  private final GameScorer<IMPL> gameScorer;

  protected AbstractGamePhaseTransitionEngine(
      final GameScorer<IMPL> gameScorer) {
    this.gameScorer = gameScorer;
  }

  @Override
  public IMPL evaluateGame(final IMPL game) {
    switch (game.getGamePhase()) {
      case Challenged:
        return evaluateChallengedPhase(game);
      case Setup:
        return evaluateSetupPhase(game);
      case Playing:
        return evaluatePlayingPhase(game);
      case RoundOver:
        return evaluateRoundOverPhase(game);
      case Declined:
        return evaluateDeclinedPhase(game);
      case NextRoundStarted:
        return evaluateNextRoundStartedPhase(game);
      case Quit:
        return evaluateQuitPhase(game);
    }
    return null;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluateSetupPhase(final IMPL game) {
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluatePlayingPhase(final IMPL game) {
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluateRoundOverPhase(final IMPL game) {
    return gameScorer.scoreGame(game);
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluateChallengedPhase(final IMPL game) {
    //  NA
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluateQuitPhase(final IMPL game) {
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluateNextRoundStartedPhase(final IMPL game) {
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL evaluateDeclinedPhase(final IMPL game) {
    return game;
  }

  @SuppressWarnings("unused")
  protected IMPL changeStateAndReevaluate(final GamePhase transitionTo, final IMPL game) {
    game.setGamePhase(transitionTo);
    return evaluateGame(game);
  }
}
