package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;
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
@SuppressWarnings("WeakerAccess")
@Component
public class NewGameHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    M extends AbstractMaskedMultiPlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractHandler<ID, P> {

  private static final Logger logger = LoggerFactory.getLogger(NewGameHandler.class);
  private final AbstractMultiPlayerGameFactory<ID, FEATURES, IMPL> gameFactory;
  private final AbstractMultiPlayerGameRepository<ID, FEATURES, IMPL> gameRepository;
  private final GameTransitionEngine<IMPL> transitionEngine;
  private final GameMasker<ID, IMPL, M> gameMasker;
  private final GamePublisher<IMPL, P> gamePublisher;
  private final GameEligibilityTracker gameTracker;

  @SuppressWarnings("WeakerAccess")
  public NewGameHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractMultiPlayerGameFactory<ID, FEATURES, IMPL> gameFactory,
      final AbstractMultiPlayerGameRepository<ID, FEATURES, IMPL> gameRepository,
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

  public M handleCreateNewGame(
      final ID initiatingPlayerID,
      final List<String> playersIDs,
      final Set<FEATURES> features) {
    //  Load as set to prevent dupes in initial setup
    Set<P> players = loadPlayerMD5s(playersIDs);

    Optional<P> foundIP = players.stream()
        .filter(x -> x.getId().equals(initiatingPlayerID))
        .findFirst();
    P initiatingPlayer = foundIP.orElseGet(() -> loadPlayer(initiatingPlayerID));

    IMPL game = setupGameWithEligibilityWrapper(initiatingPlayer, features, players);

    gamePublisher.publish(game, initiatingPlayer);

    return gameMasker.maskGameForPlayer(game, initiatingPlayer);
  }

  private IMPL setupGameWithEligibilityWrapper(final P initiatingPlayer,
      final Set<FEATURES> features, Set<P> players) {
    PlayerGameEligibilityResult eligibilityResult = gameTracker
        .getGameEligibility(initiatingPlayer);

    if (eligibilityResult != null &&
        PlayerGameEligibility.NoGamesAvailable == eligibilityResult.getEligibility()) {
      throw new OutOfGamesForTodayException();
    }

    IMPL game;
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

  private IMPL setupGame(final Set<FEATURES> features, final Set<P> players,
      final P initiatingPlayer) {
    IMPL game = gameFactory.createGame(
        features,
        new LinkedList<>(players),
        initiatingPlayer);
    game = transitionEngine.evaluateGame(game);
    return gameRepository.save(game);
  }
}
