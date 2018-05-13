package com.jtbdevelopment.games.factory.gamevalidators;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.factory.GameValidator;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
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
public class PlayersActiveGameValidator<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>>
    implements GameValidator<IMPL> {

  private static final String ERROR_MESSAGE = "Game contains inactive players.";
  private final AbstractPlayerRepository<ID, P> playerRepository;

  public PlayersActiveGameValidator(
      @SuppressWarnings("SpringJavaAutowiringInspection") final AbstractPlayerRepository<ID, P> playerRepository
  ) {
    this.playerRepository = playerRepository;
  }

  @Override
  public boolean validateGame(final IMPL game) {
    if (game instanceof AbstractMultiPlayerGame) {
      //noinspection unchecked
      AbstractMultiPlayerGame<ID, FEATURES> multiPlayerGame = (AbstractMultiPlayerGame<ID, FEATURES>) game;
      List<Player<ID>> players = multiPlayerGame.getPlayers();
      List<ID> ids = players.stream().map(Player::getId).collect(Collectors.toList());
      Iterable<? extends AbstractPlayer<ID>> loaded = playerRepository.findAllById(ids);
      long active = StreamSupport.stream(loaded.spliterator(), false)
          .filter(x -> !x.isDisabled())
          .count();
      return active == players.size();
    } else if (game instanceof AbstractSinglePlayerGame) {
      //noinspection unchecked
      AbstractSinglePlayerGame<ID, FEATURES> singPlayerGame = (AbstractSinglePlayerGame<ID, FEATURES>) game;
      Optional<? extends AbstractPlayer<ID>> loaded = playerRepository
          .findById(singPlayerGame.getPlayer().getId());
      return loaded.isPresent() && !loaded.get().isDisabled();
    }

    throw new IllegalArgumentException("unsupported type");
  }

  @Override
  public String errorMessage() {
    return ERROR_MESSAGE;
  }
}
