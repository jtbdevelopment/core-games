package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException;
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.io.Serializable;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 11/19/14 Time: 7:01 AM
 */
public class AbstractGameGetterHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    P extends Player<ID>>
    extends AbstractHandler<ID, P> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractGameGetterHandler.class);
  protected final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository;

  protected AbstractGameGetterHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository) {
    super(playerRepository);
    this.gameRepository = gameRepository;
  }

  void validatePlayerForGame(final Game game, final Player player) {
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

  IMPL loadGame(final ID gameID) {
    Optional<IMPL> optional = gameRepository.findById(gameID);
    if (optional.isPresent()) {
      return optional.get();
    }

    logger.info("Game was not loaded " + gameID);
    throw new FailedToFindGameException();
  }
}
