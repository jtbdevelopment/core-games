package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException;
import com.jtbdevelopment.games.state.Game;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 4/4/2015 Time: 8:43 PM
 */
public abstract class AbstractGameFactory<IMPL extends Game> {

  private final List<GameInitializer<IMPL>> gameInitializers;
  private final List<GameValidator<IMPL>> gameValidators;

  public AbstractGameFactory(
      List<GameInitializer<IMPL>> gameInitializers,
      List<GameValidator<IMPL>> gameValidators) {
    this.gameInitializers = gameInitializers;
    this.gameValidators = gameValidators;
  }

  protected abstract IMPL newGame();

  protected void copyFromPreviousGame(final IMPL previousGame, final IMPL newGame) {
    newGame.setRound(previousGame.getRound() + 1);
    //noinspection unchecked
    newGame.setPreviousId(previousGame.getId());
  }

  protected IMPL prepareGame(final IMPL game) {
    initializeGame(game);
    validateGame(game);
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected void initializeGame(final IMPL game) {
    gameInitializers.forEach(i -> i.initializeGame(game));
  }

  @SuppressWarnings("WeakerAccess")
  protected void validateGame(final IMPL game) {
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
