package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import java.io.Serializable;
import org.springframework.stereotype.Component;

/**
 * Date: 11/17/14 Time: 6:37 AM
 */
@Component
public class GameGetterHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends Player<ID>>
    extends AbstractGameGetterHandler<ID, FEATURES, IMPL, P> {

  private final GameMasker<ID, IMPL, M> gameMasker;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  public GameGetterHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      final GameMasker<ID, IMPL, M> gameMasker) {
    super(playerRepository, gameRepository);
    this.gameMasker = gameMasker;
  }

  public Game getGame(final ID playerID, final ID gameID) {
    Player<ID> player = loadPlayer(playerID);
    IMPL game = loadGame(gameID);
    validatePlayerForGame(game, player);
    return gameMasker.maskGameForPlayer(game, player);
  }
}
