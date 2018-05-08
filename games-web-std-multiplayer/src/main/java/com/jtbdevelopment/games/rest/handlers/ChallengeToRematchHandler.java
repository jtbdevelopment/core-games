package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/2014 Time: 9:11 PM
 */
@Component
public class ChallengeToRematchHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameActionHandler<Object, ID, FEATURES, IMPL, M, P> {

  private final AbstractMultiPlayerGameFactory<ID, FEATURES, IMPL> gameFactory;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  ChallengeToRematchHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      final GameTransitionEngine<IMPL> transitionEngine,
      final GamePublisher<IMPL, P> gamePublisher,
      final GameEligibilityTracker gameTracker,
      final GameMasker<ID, IMPL, M> gameMasker,
      final AbstractMultiPlayerGameFactory<ID, FEATURES, IMPL> gameFactory) {
    super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker,
        gameMasker);
    this.gameFactory = gameFactory;
  }

  @Override
  protected boolean requiresEligibilityCheck(final Object param) {
    return true;
  }

  @Override
  protected IMPL handleActionInternal(
      final P player,
      final IMPL previousGame,
      final Object param) {
    if (!previousGame.getGamePhase().equals(GamePhase.RoundOver)) {
      throw new GameIsNotAvailableToRematchException();
    }

    previousGame.setRematchTimestamp(Instant.now());
    IMPL evaluateGame = transitionEngine.evaluateGame(previousGame);
    IMPL saved = gameRepository.save(evaluateGame);
    IMPL transitioned = gamePublisher.publish(saved, null);
    //  We set to system player so it gets published to all players, including this one
    //  TODO - handle newGame setup failing..
    return setupGame(transitioned, player);
  }

  private IMPL setupGame(final IMPL previousGame, final P initiatingPlayer) {
    return gameFactory.createGame(previousGame, initiatingPlayer);
  }
}
