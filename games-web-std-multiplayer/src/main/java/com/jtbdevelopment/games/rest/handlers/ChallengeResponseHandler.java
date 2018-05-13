package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.TooLateToRespondToChallengeException;
import com.jtbdevelopment.games.players.AbstractPlayer;
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
 * Date: 11/9/2014 Time: 5:27 PM
 */
@Component
public class ChallengeResponseHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameActionHandler<PlayerState, ID, FEATURES, IMPL, M, P> {

  ChallengeResponseHandler(
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
  protected boolean requiresEligibilityCheck(final PlayerState param) {
    return PlayerState.Accepted.equals(param);
  }

  @Override
  protected IMPL handleActionInternal(final P player, final IMPL game, final PlayerState param) {
    // We will at least record further ack/nacks for information
    if (game.getGamePhase().equals(GamePhase.Challenged) ||
        game.getGamePhase().equals(GamePhase.Declined)) {
      //  Players can change their mind in the server side
      game.getPlayerStates().put(player.getId(), param);
      return game;
    } else {
      throw new TooLateToRespondToChallengeException();
    }

  }

}
