package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.factory.AbstractSinglePlayerGameFactory;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.io.Serializable;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/2014 Time: 9:10 PM
 */
@SuppressWarnings("WeakerAccess")
@Component
public class NewGameHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractSinglePlayerGame<ID, FEATURES>,
    M extends AbstractMaskedSinglePlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractHandler<ID, P> {

  private static final Logger logger = LoggerFactory.getLogger(NewGameHandler.class);
  private final AbstractSinglePlayerGameFactory<ID, FEATURES, IMPL> gameFactory;
  private final AbstractSinglePlayerGameRepository<ID, FEATURES, IMPL> gameRepository;
  private final GameTransitionEngine<IMPL> transitionEngine;
  private final GameMasker<ID, IMPL, M> gameMasker;
  private final GamePublisher<IMPL, P> gamePublisher;
  private final GameEligibilityTracker gameTracker;

  @SuppressWarnings("WeakerAccess")
  public NewGameHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractSinglePlayerGameFactory<ID, FEATURES, IMPL> gameFactory,
      final AbstractSinglePlayerGameRepository<ID, FEATURES, IMPL> gameRepository,
      final GameTransitionEngine<IMPL> transitionEngine,
      final GameMasker<ID, IMPL, M> gameMasker,
      final GamePublisher<IMPL, P> gamePublisher,
      final GameEligibilityTracker gameTracker) {
    super(playerRepository);
    this.gameFactory = gameFactory;
    this.gameRepository = gameRepository;
    this.transitionEngine = transitionEngine;
    this.gameMasker = gameMasker;
    this.gamePublisher = gamePublisher;
    this.gameTracker = gameTracker;
  }

  public M handleCreateNewGame(final ID playerID, final Set<FEATURES> features) {
    P player = loadPlayer(playerID);//  Load as set to prevent dupes in initial setup
    IMPL game = setupGameWithEligibilityWrapper(features, player);

    gamePublisher.publish(game, player);
    return gameMasker.maskGameForPlayer(game, player);
  }

  private IMPL setupGameWithEligibilityWrapper(
      final Set<FEATURES> features,
      final Player<ID> player) {
    PlayerGameEligibilityResult eligibilityResult = gameTracker.getGameEligibility(player);
    if (eligibilityResult != null && eligibilityResult.getEligibility()
        .equals(PlayerGameEligibility.NoGamesAvailable)) {
      throw new OutOfGamesForTodayException();
    }

    try {
      return setupGame(features, player);
    } catch (Exception e) {
      try {
        if (eligibilityResult != null) {
          gameTracker.revertGameEligibility(eligibilityResult);
        }

      } catch (Exception e2) {
        //  TODO - notification
        logger.warn("Failed to revert players game eligibility " + eligibilityResult, e2);
      }

      throw e;
    }
  }

  private IMPL setupGame(
      final Set<FEATURES> features,
      final Player<ID> player) {
    IMPL game = gameFactory.createGame(features, player);
    game = transitionEngine.evaluateGame(game);

    return gameRepository.save(game);
  }
}
