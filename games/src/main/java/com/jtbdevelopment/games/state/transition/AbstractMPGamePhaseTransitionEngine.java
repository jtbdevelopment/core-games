package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import java.util.Optional;

/**
 * Date: 4/8/2015 Time: 8:22 PM
 *
 * You will most likely need to override the evaluate setup and playing functions
 */
public abstract class AbstractMPGamePhaseTransitionEngine<IMPL extends MultiPlayerGame>
    implements GameTransitionEngine<IMPL> {

  private final GameScorer<IMPL> gameScorer;

  public AbstractMPGamePhaseTransitionEngine(final GameScorer<IMPL> gameScorer) {
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
    if (game.getRematchTimestamp() != null) {
      return changeStateAndReevaluate(GamePhase.NextRoundStarted, game);
    }

    return gameScorer.scoreGame(game);
  }

  @SuppressWarnings("WeakerAccess")
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

  @SuppressWarnings("WeakerAccess")
  protected IMPL changeStateAndReevaluate(final GamePhase transitionTo, final IMPL game) {
    game.setGamePhase(transitionTo);
    return evaluateGame(game);
  }
}
