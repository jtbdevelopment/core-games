package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotPossibleToQuitNowException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.PlayerState;
import org.springframework.stereotype.Component;

/**
 * Date: 11/28/2014 Time: 7:40 PM
 */
@Component
public class QuitHandler extends AbstractGameActionHandler<Object, MultiPlayerGame> {

  @Override
  protected MultiPlayerGame handleActionInternal(final Player player, final MultiPlayerGame game,
      final Object param) {
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
