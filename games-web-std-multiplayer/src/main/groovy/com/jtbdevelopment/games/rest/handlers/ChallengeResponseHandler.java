package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.exceptions.input.TooLateToRespondToChallengeException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.PlayerState;
import org.springframework.stereotype.Component;

/**
 * Date: 11/9/2014
 * Time: 5:27 PM
 */
@Component
public class ChallengeResponseHandler extends
    AbstractGameActionHandler<PlayerState, MultiPlayerGame> {

  @Override
  protected boolean requiresEligibilityCheck(final PlayerState param) {
    return PlayerState.Accepted.equals(param);
  }

  @Override
  protected MultiPlayerGame handleActionInternal(
      final Player player,
      final MultiPlayerGame game,
      final PlayerState param) {
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
