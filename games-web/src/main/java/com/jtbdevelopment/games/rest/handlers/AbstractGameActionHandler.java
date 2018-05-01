package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 11/9/2014 Time: 8:36 PM
 */
public abstract class AbstractGameActionHandler<T, IMPL extends Game> extends
    AbstractGameGetterHandler {

  private static final Logger logger = LoggerFactory.getLogger(AbstractGameActionHandler.class);
  @Autowired
  protected GameTransitionEngine transitionEngine;
  @Autowired
  protected GamePublisher gamePublisher;
  @Autowired
  protected GameEligibilityTracker gameTracker;
  @Autowired
  protected GameMasker gameMasker;

  protected abstract IMPL handleActionInternal(final Player player, final IMPL game, final T param);

  public Game handleAction(final Serializable playerID, final Serializable gameID, final T param) {
    Player player = loadPlayer(playerID);
    IMPL game = (IMPL) loadGame(gameID);
    validatePlayerForGame(game, player);
    Game updatedGame = updateGameWithEligibilityWrapper(player, game, param);

    updatedGame = gamePublisher.publish(updatedGame, player);
    return gameMasker.maskGameForPlayer(updatedGame, player);

  }

  public Game handleAction(final Serializable playerID, final Serializable gameID) {
    return handleAction(playerID, gameID, null);
  }

  protected Game updateGameWithEligibilityWrapper(final Player player, final IMPL game,
      final T param) {
    Game updatedGame;
    PlayerGameEligibilityResult eligibilityResult = null;
    if (requiresEligibilityCheck(param)) {
      eligibilityResult = gameTracker.getGameEligibility(player);
      if (eligibilityResult.getEligibility().equals(PlayerGameEligibility.NoGamesAvailable)) {
        throw new OutOfGamesForTodayException();
      }

    }

    try {
      updatedGame = updateGame(player, game, param);
    } catch (Exception e) {
      try {
        if (eligibilityResult != null) {
          gameTracker.revertGameEligibility(eligibilityResult);
        }

      } catch (Throwable e2) {
        //  TODO - notify
        logger.warn("Failed to revert players game eligibility " + eligibilityResult, e2);
      }

      throw e;
    }

    return updatedGame;
  }

  protected Game updateGame(final Player player, final IMPL game, final T param) {
    IMPL updated = rotateTurnBasedGame(handleActionInternal(player, game, param));
    updated = ((IMPL) (transitionEngine.evaluateGame(updated)));
    return gameRepository.save(updated);
  }

  protected IMPL rotateTurnBasedGame(final IMPL game) {
    return game;
  }

  protected boolean requiresEligibilityCheck(final T param) {
    return false;
  }
}
