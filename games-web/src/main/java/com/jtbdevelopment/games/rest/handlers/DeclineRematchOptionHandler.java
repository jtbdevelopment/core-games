package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import java.io.Serializable;
import org.springframework.stereotype.Component;

/**
 * Date: 8/23/15 Time: 5:14 PM
 */
@Component
public class DeclineRematchOptionHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameActionHandler<Object, ID, FEATURES, IMPL, M, P> {

  @SuppressWarnings("SpringJavaAutowiringInspection")
  public DeclineRematchOptionHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      final GameTransitionEngine<IMPL> transitionEngine,
      final GamePublisher<IMPL, P> gamePublisher,
      final GameEligibilityTracker gameTracker,
      final GameMasker<ID, IMPL, M> gameMasker) {
    super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker,
        gameMasker);
  }

  @Override
  protected IMPL handleActionInternal(
      final P player,
      final IMPL game,
      final Object param) {
    if (!game.getGamePhase().equals(GamePhase.RoundOver)) {
      throw new GameIsNotAvailableToRematchException();
    }

    game.setGamePhase(GamePhase.NextRoundStarted);
    return game;
  }
}
