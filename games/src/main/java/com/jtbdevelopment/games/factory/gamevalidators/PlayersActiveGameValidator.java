package com.jtbdevelopment.games.factory.gamevalidators;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.factory.GameValidator;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;

/**
 * Date: 11/8/14 Time: 8:55 AM
 */
@Component
public class PlayersActiveGameValidator<ID extends Serializable> implements
    GameValidator<Game<ID, ?, ?>> {

  private static final String ERROR_MESSAGE = "Game contains inactive players.";
  private final AbstractPlayerRepository<ID, ? extends Player<ID>> playerRepository;

  public PlayersActiveGameValidator(
      @SuppressWarnings("SpringJavaAutowiringInspection") final AbstractPlayerRepository<ID, ? extends Player<ID>> playerRepository
  ) {
    this.playerRepository = playerRepository;
  }

  @Override
  public boolean validateGame(final Game<ID, ?, ?> game) {
    if (game instanceof MultiPlayerGame) {
      //noinspection unchecked
      MultiPlayerGame<ID, ?, ?> multiPlayerGame = (MultiPlayerGame<ID, ?, ?>) game;
      List<Player<ID>> players = multiPlayerGame.getPlayers();
      List<ID> ids = players.stream().map(Player::getId).collect(Collectors.toList());
      Iterable<? extends Player<ID>> loaded = playerRepository.findAllById(ids);
      long active = StreamSupport.stream(loaded.spliterator(), false)
          .filter(x -> !x.getDisabled())
          .count();
      return active == players.size();
    } else if (game instanceof SinglePlayerGame) {
      //noinspection unchecked
      Optional<? extends Player<ID>> loaded = playerRepository
          .findById(((SinglePlayerGame<ID, ?, ?>) game).getPlayer().getId());
      return loaded.isPresent() && !loaded.get().getDisabled();
    }

    throw new IllegalArgumentException("unsupported type");
  }

  @Override
  public String errorMessage() {
    return ERROR_MESSAGE;
  }
}
