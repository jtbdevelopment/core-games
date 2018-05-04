package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException;
import com.jtbdevelopment.games.state.Game;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 4/4/2015 Time: 8:43 PM
 */
public abstract class AbstractGameFactory<ID extends Serializable, IMPL extends Game<ID, ?, ?>> {

  private final List<GameInitializer<IMPL>> gameInitializers;
  private final List<GameValidator<IMPL>> gameValidators;

  //  keep generic to avoid spring instantiation being overly specific
  @SuppressWarnings({"unchecked", "WeakerAccess"})
  protected AbstractGameFactory(
      List<GameInitializer> gameInitializers,
      List<GameValidator> gameValidators) {
    this.gameInitializers = gameInitializers.stream()
        .map(i -> (GameInitializer<IMPL>) i)
        .collect(Collectors.toList());
    this.gameValidators = gameValidators.stream()
        .map(v -> (GameValidator<IMPL>) v)
        .collect(Collectors.toList());
  }

  protected abstract IMPL newGame();

  @SuppressWarnings("WeakerAccess")
  protected void copyFromPreviousGame(final IMPL previousGame, final IMPL newGame) {
    newGame.setRound(previousGame.getRound() + 1);
    newGame.setPreviousId(previousGame.getId());
  }

  @SuppressWarnings("WeakerAccess")
  protected void prepareGame(final IMPL game) {
    initializeGame(game);
    validateGame(game);
  }

  @SuppressWarnings("WeakerAccess")
  private void initializeGame(final IMPL game) {
    gameInitializers.forEach(i -> i.initializeGame(game));
  }

  @SuppressWarnings("WeakerAccess")
  private void validateGame(final IMPL game) {
    List<GameValidator> failedChecks = gameValidators
        .stream()
        .filter(v -> !v.validateGame(game))
        .collect(Collectors.toList());
    if (!failedChecks.isEmpty()) {
      throw new FailedToCreateValidGameException(
          failedChecks
              .stream()
              .map(GameValidator::errorMessage)
              .collect(Collectors.joining("  ")));
    }
  }
}
