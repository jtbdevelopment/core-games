package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.util.List;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 8:43 PM
 */
public abstract class AbstractSinglePlayerGameFactory<IMPL extends SinglePlayerGame, FEATURES> extends
    AbstractGameFactory<IMPL> implements SinglePlayerGameFactory<IMPL, FEATURES> {

  public AbstractSinglePlayerGameFactory(final List<GameInitializer<IMPL>> gameInitializers,
      final List<GameValidator<IMPL>> gameValidators) {
    super(gameInitializers, gameValidators);
  }

  public IMPL createGame(final Set<FEATURES> features, final Player player) {
    IMPL game = createFreshGame(features, player);

    prepareGame(game);
    return game;
  }

  public IMPL createGame(final IMPL previousGame) {
    IMPL game = (IMPL) createFreshGame(previousGame.getFeatures(), previousGame.getPlayer());
    copyFromPreviousGame(previousGame, game);
    prepareGame(game);
    return game;
  }

  protected IMPL createFreshGame(final Set<FEATURES> features, final Player player) {
    IMPL game = newGame();
    game.setPlayer(player);
    game.setVersion(null);
    game.getFeatures().addAll(features);
    game.setGamePhase(GamePhase.Setup);
    return game;
  }

}
