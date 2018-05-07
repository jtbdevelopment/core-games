package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 8:43 PM
 */
public abstract class AbstractMultiPlayerGameFactory<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>> extends
    AbstractGameFactory<ID, FEATURES, IMPL> implements MultiPlayerGameFactory<ID, FEATURES, IMPL> {

  @SuppressWarnings("WeakerAccess")
  protected AbstractMultiPlayerGameFactory(
      final List<GameInitializer> gameInitializers,
      final List<GameValidator> gameValidators) {
    super(gameInitializers, gameValidators);
  }

  @Override
  public IMPL createGame(
      final Set<FEATURES> features,
      final List<Player<ID>> players,
      final Player<ID> initiatingPlayer) {
    IMPL game = createFreshGame(features, players, initiatingPlayer);

    prepareGame(game);
    return game;
  }

  @Override
  public IMPL createGame(final IMPL previousGame, final Player<ID> initiatingPlayer) {
    List<Player<ID>> players = rotatePlayers(previousGame);
    IMPL game = createFreshGame(previousGame.getFeatures(), players, initiatingPlayer);
    copyFromPreviousGame(previousGame, game);
    prepareGame(game);
    return game;
  }

  private List<Player<ID>> rotatePlayers(final IMPL previousGame) {
    List<Player<ID>> players = new ArrayList<>();
    players.addAll(previousGame.getPlayers());
    players.add(players.remove(0));
    return players;
  }

  private IMPL createFreshGame(
      final Set<FEATURES> features,
      final List<Player<ID>> players,
      final Player<ID> initiatingPlayer) {
    IMPL game = newGame();
    game.setRound(1);
    game.setGamePhase(GamePhase.Challenged);
    game.setVersion(null);
    game.getFeatures().addAll(features);
    game.setInitiatingPlayer(initiatingPlayer.getId());
    game.getPlayers().addAll(players);
    if (!game.getPlayers().contains(initiatingPlayer)) {
      game.getPlayers().add(initiatingPlayer);
    }

    return game;
  }

}
