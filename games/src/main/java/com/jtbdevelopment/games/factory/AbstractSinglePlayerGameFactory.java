package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 8:43 PM
 */
public abstract class AbstractSinglePlayerGameFactory<
    ID extends Serializable,
    FEATURES,
    IMPL extends SinglePlayerGame<ID, ?, FEATURES>> extends
    AbstractGameFactory<ID, IMPL> implements SinglePlayerGameFactory<ID, FEATURES, IMPL> {

  @SuppressWarnings("WeakerAccess")
  protected AbstractSinglePlayerGameFactory(
      final List<GameInitializer> gameInitializers,
      final List<GameValidator> gameValidators) {
    super(gameInitializers, gameValidators);
  }

  public IMPL createGame(final Set<FEATURES> features, final Player<ID> player) {
    IMPL game = createFreshGame(features, player);

    prepareGame(game);
    return game;
  }

  public IMPL createGame(final IMPL previousGame) {
    IMPL game = createFreshGame(previousGame.getFeatures(), previousGame.getPlayer());
    copyFromPreviousGame(previousGame, game);
    prepareGame(game);
    return game;
  }

  private IMPL createFreshGame(final Set<FEATURES> features, final Player<ID> player) {
    IMPL game = newGame();
    game.setPlayer(player);
    game.setVersion(null);
    game.getFeatures().addAll(features);
    game.setGamePhase(GamePhase.Setup);
    game.setRound(1);
    return game;
  }

}
