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
public class PlayersActiveGameValidator implements GameValidator<Game> {

  private static final String ERROR_MESSAGE = "Game contains inactive players.";
  private final AbstractPlayerRepository playerRepository;

  public PlayersActiveGameValidator(
      final AbstractPlayerRepository playerRepository
  ) {
    this.playerRepository = playerRepository;
  }

  @Override
  public boolean validateGame(final Game game) {
    if (game instanceof MultiPlayerGame) {
      MultiPlayerGame multiPlayerGame = (MultiPlayerGame) game;
      List<Player> players = (List<Player>) multiPlayerGame.getPlayers();
      List<Serializable> ids = players.stream().map(Player::getId).collect(Collectors.toList());
      Iterable<? extends Player> loaded = playerRepository.findAllById(ids);
      long active = StreamSupport.stream(loaded.spliterator(), false).filter(x -> !x.getDisabled())
          .count();
      return active == players.size();
    } else if (game instanceof SinglePlayerGame) {
      Optional<? extends Player> loaded = playerRepository
          .findById(((SinglePlayerGame) game).getPlayer().getId());
      return loaded.isPresent() && !loaded.get().getDisabled();
    }

    throw new IllegalArgumentException("unsupported type");
  }

  @Override
  public String errorMessage() {
    return ERROR_MESSAGE;
  }
}
