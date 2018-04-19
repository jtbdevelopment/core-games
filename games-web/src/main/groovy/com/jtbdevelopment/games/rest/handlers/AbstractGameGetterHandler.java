package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException;
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.io.Serializable;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 11/19/14 Time: 7:01 AM
 */
public class AbstractGameGetterHandler extends AbstractHandler {

  private static final Logger logger = LoggerFactory.getLogger(AbstractGameGetterHandler.class);
  @Autowired
  protected AbstractGameRepository gameRepository;

  protected void validatePlayerForGame(final Game game, final Player player) {
    if (game instanceof MultiPlayerGame) {
      if (!game.getAllPlayers().contains(player)) {
        throw new PlayerNotPartOfGameException();
      }

    } else if (game instanceof SinglePlayerGame) {
      if (!((SinglePlayerGame) game).getPlayer().equals(player)) {
        throw new PlayerNotPartOfGameException();
      }

    }

  }

  protected Game loadGame(final Serializable gameID) {
    Optional<? extends Game> optional = gameRepository.findById(gameID);
    if (optional.isPresent()) {
      return optional.get();
    }

    logger.info("Game was not loaded " + gameID);
    throw new FailedToFindGameException();
  }
}
