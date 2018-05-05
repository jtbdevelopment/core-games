package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.factory.AbstractSinglePlayerGameFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.masking.MaskedSinglePlayerGame;
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
@Component
public class NewGameHandler<ID extends Serializable, FEATURES> extends AbstractHandler {

  private static final Logger logger = LoggerFactory.getLogger(NewGameHandler.class);
  private final AbstractSinglePlayerGameFactory<ID, FEATURES, SinglePlayerGame<ID, ?, FEATURES>> gameFactory;
  private final AbstractSinglePlayerGameRepository<ID, ?, FEATURES, SinglePlayerGame<ID, ?, FEATURES>> gameRepository;
  private final GameTransitionEngine<SinglePlayerGame> transitionEngine;
  private final GameMasker<ID, SinglePlayerGame<ID, ?, FEATURES>, MaskedSinglePlayerGame<FEATURES>> gameMasker;
  private final GamePublisher<SinglePlayerGame> gamePublisher;
  private final GameEligibilityTracker<PlayerGameEligibilityResult> gameTracker;

  NewGameHandler(
      final AbstractSinglePlayerGameFactory<ID, FEATURES, SinglePlayerGame<ID, ?, FEATURES>> gameFactory,
      final AbstractSinglePlayerGameRepository<ID, ?, FEATURES, SinglePlayerGame<ID, ?, FEATURES>> gameRepository,
      final GameTransitionEngine<SinglePlayerGame> transitionEngine,
      final GameMasker<ID, SinglePlayerGame<ID, ?, FEATURES>, MaskedSinglePlayerGame<FEATURES>> gameMasker,
      final GamePublisher<SinglePlayerGame> gamePublisher,
      final GameEligibilityTracker<PlayerGameEligibilityResult> gameTracker) {
    this.gameFactory = gameFactory;
    this.gameRepository = gameRepository;
    this.transitionEngine = transitionEngine;
    this.gameMasker = gameMasker;
    this.gamePublisher = gamePublisher;
    this.gameTracker = gameTracker;
  }

  public Game handleCreateNewGame(final ID playerID, final Set<FEATURES> features) {
    Player<ID> player = loadPlayer(playerID);//  Load as set to prevent dupes in initial setup
    SinglePlayerGame game = setupGameWithEligibilityWrapper(features, player);

    gamePublisher.publish(game, player);
    return gameMasker.maskGameForPlayer(game, player);
  }

  private SinglePlayerGame setupGameWithEligibilityWrapper(final Set<FEATURES> features,
      Player<ID> player) {
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

  private SinglePlayerGame<ID, ?, FEATURES> setupGame(final Set<FEATURES> features,
      final Player<ID> player) {
    SinglePlayerGame<ID, ?, FEATURES> game = gameFactory.createGame(features, player);
    game = transitionEngine.evaluateGame(game);

    return gameRepository.save(game);
  }
}
