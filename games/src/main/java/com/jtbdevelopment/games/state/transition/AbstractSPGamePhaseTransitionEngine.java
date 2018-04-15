package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.scoring.GameScorer;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.SinglePlayerGame;

/**
 * Date: 4/8/2015 Time: 8:22 PM
 *
 * You will most likely need to override the evaluate setup and playing functions
 */
public abstract class AbstractSPGamePhaseTransitionEngine<IMPL extends SinglePlayerGame>
    implements GameTransitionEngine<IMPL> {

  private final GameScorer<IMPL> gameScorer;

  public AbstractSPGamePhaseTransitionEngine(
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
    //  TODO
        /*
        if (game.rematchTimestamp != null) {
            return changeStateAndReevaluate(GamePhase.NextRoundStarted, game)
        }
        */
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

  protected IMPL changeStateAndReevaluate(final GamePhase transitionTo, final IMPL game) {
    game.setGamePhase(transitionTo);
    return evaluateGame(game);
  }
}
