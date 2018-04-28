package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/2014 Time: 9:10 PM
 */
@Component
public class NewGameHandler extends AbstractHandler {

  private static final Logger logger = LoggerFactory.getLogger(NewGameHandler.class);
  protected final AbstractMultiPlayerGameFactory gameFactory;
  protected final AbstractMultiPlayerGameRepository gameRepository;
  protected final GameTransitionEngine transitionEngine;
  protected final GameMasker gameMasker;
  protected final GamePublisher gamePublisher;
  protected final GameEligibilityTracker gameTracker;

  public NewGameHandler(
      final AbstractMultiPlayerGameFactory gameFactory,
      final AbstractMultiPlayerGameRepository gameRepository,
      final GameTransitionEngine transitionEngine,
      final GameMasker gameMasker,
      final GamePublisher gamePublisher,
      final GameEligibilityTracker gameTracker) {
    this.gameFactory = gameFactory;
    this.gameRepository = gameRepository;
    this.transitionEngine = transitionEngine;
    this.gameMasker = gameMasker;
    this.gamePublisher = gamePublisher;
    this.gameTracker = gameTracker;
  }

  public Game handleCreateNewGame(final Serializable initiatingPlayerID,
      final List<String> playersIDs, final Set<?> features) {
    //  Load as set to prevent dupes in initial setup
    Set<Player> players = loadPlayerMD5s(playersIDs);

    Optional<Player> foundIP = players.stream().filter(x -> x.getId().equals(initiatingPlayerID))
        .findFirst();
    Player initiatingPlayer = foundIP.orElseGet(() -> loadPlayer(initiatingPlayerID));

    Game game = setupGameWithEligibilityWrapper(initiatingPlayer, features, players);

    gamePublisher.publish(game, initiatingPlayer);

    return gameMasker.maskGameForPlayer(game, initiatingPlayer);
  }

  private Game setupGameWithEligibilityWrapper(final Player initiatingPlayer,
      final Set<?> features, Set<Player> players) {
    PlayerGameEligibilityResult eligibilityResult = gameTracker
        .getGameEligibility(initiatingPlayer);
    ;

    if (eligibilityResult != null &&
        PlayerGameEligibility.NoGamesAvailable == eligibilityResult.getEligibility()) {
      throw new OutOfGamesForTodayException();
    }

    Game game;
    try {
      game = setupGame(features, players, initiatingPlayer);
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

    return game;
  }

  private Game setupGame(final Set<?> features, final Set<Player> players,
      final Player initiatingPlayer) {
    MultiPlayerGame game = gameFactory.createGame(
        features,
        new LinkedList<>(players),
        initiatingPlayer);
    game = ((MultiPlayerGame) (transitionEngine.evaluateGame(game)));
    return gameRepository.save(game);
  }
}
