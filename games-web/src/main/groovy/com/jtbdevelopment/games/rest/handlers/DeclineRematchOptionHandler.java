package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 8/23/15 Time: 5:14 PM
 */
@Component
@CompileStatic
public class DeclineRematchOptionHandler extends
    AbstractGameActionHandler<Object, MultiPlayerGame> {

  @Autowired
  protected AbstractMultiPlayerGameFactory gameFactory;

  @Override
  protected MultiPlayerGame handleActionInternal(final Player player, final MultiPlayerGame game,
      final Object param) {
    if (!game.getGamePhase().equals(GamePhase.RoundOver)) {
      throw new GameIsNotAvailableToRematchException();
    }

    game.setGamePhase(GamePhase.NextRoundStarted);
    return game;
  }
}
