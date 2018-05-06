package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotPossibleToQuitNowException;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
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
    P extends AbstractPlayer<ID>>
    extends AbstractGameActionHandler<Object, ID, FEATURES, IMPL, P> {

  public QuitHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository) {
    super(playerRepository, gameRepository);
  }

  @Override
  protected IMPL handleActionInternal(final P player, final IMPL game,
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
