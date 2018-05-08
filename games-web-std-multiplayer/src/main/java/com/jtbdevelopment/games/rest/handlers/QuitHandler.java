package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotPossibleToQuitNowException;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import java.io.Serializable;
import org.springframework.stereotype.Component;

/**
 * Date: 11/28/2014 Time: 7:40 PM
 */
@Component
public class QuitHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameActionHandler<Object, ID, FEATURES, IMPL, M, P> {

  @SuppressWarnings("SpringJavaAutowiringInspection")
  QuitHandler(
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
  protected IMPL handleActionInternal(final P player, final IMPL game, final Object param) {
    if (game.getGamePhase().equals(GamePhase.NextRoundStarted) || game.getGamePhase()
        .equals(GamePhase.RoundOver) || game.getGamePhase().equals(GamePhase.Quit) || game
        .getGamePhase().equals(GamePhase.Declined)) {
      throw new GameIsNotPossibleToQuitNowException();
    }

    game.setGamePhase(GamePhase.Quit);
    game.getPlayerStates().put(player.getId(), PlayerState.Quit);
    return game;
  }

}
