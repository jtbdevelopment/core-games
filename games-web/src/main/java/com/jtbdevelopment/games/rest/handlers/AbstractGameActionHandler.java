package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 11/9/2014 Time: 8:36 PM
 */
public abstract class AbstractGameActionHandler<
    PARAMTYPE,
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>> extends
    AbstractGameGetterHandler<ID, FEATURES, IMPL, P> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractGameActionHandler.class);
  @SuppressWarnings("WeakerAccess")
  protected final GameTransitionEngine<IMPL> transitionEngine;
  @SuppressWarnings("WeakerAccess")
  protected final GamePublisher<IMPL, P> gamePublisher;
  private final GameEligibilityTracker gameTracker;
  private final GameMasker<ID, IMPL, M> gameMasker;

  public AbstractGameActionHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      final GameTransitionEngine<IMPL> transitionEngine,
      final GamePublisher<IMPL, P> gamePublisher,
      final GameEligibilityTracker gameTracker,
      final GameMasker<ID, IMPL, M> gameMasker) {
    super(playerRepository, gameRepository);
    this.transitionEngine = transitionEngine;
    this.gamePublisher = gamePublisher;
    this.gameTracker = gameTracker;
    this.gameMasker = gameMasker;
  }

  protected abstract IMPL handleActionInternal(final P player, final IMPL game,
      final PARAMTYPE param);

  public M handleAction(final ID playerID, final ID gameID, final PARAMTYPE param) {
    P player = loadPlayer(playerID);
    IMPL game = loadGame(gameID);
    validatePlayerForGame(game, player);
    IMPL updatedGame = updateGameWithEligibilityWrapper(player, game, param);

    updatedGame = gamePublisher.publish(updatedGame, player);
    return gameMasker.maskGameForPlayer(updatedGame, player);

  }

  public M handleAction(final ID playerID, final ID gameID) {
    return handleAction(playerID, gameID, null);
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL updateGameWithEligibilityWrapper(final P player, final IMPL game,
      final PARAMTYPE param) {
    IMPL updatedGame;
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

  private IMPL updateGame(final P player, final IMPL game, final PARAMTYPE param) {
    IMPL updated = rotateTurnBasedGame(handleActionInternal(player, game, param));
    updated = transitionEngine.evaluateGame(updated);
    return gameRepository.save(updated);
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL rotateTurnBasedGame(final IMPL game) {
    return game;
  }

  protected boolean requiresEligibilityCheck(final PARAMTYPE param) {
    return false;
  }
}
