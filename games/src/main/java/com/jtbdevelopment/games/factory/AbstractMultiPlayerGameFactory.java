package com.jtbdevelopment.games.factory;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Date: 4/4/2015 Time: 8:43 PM
 */
public abstract class AbstractMultiPlayerGameFactory<IMPL extends MultiPlayerGame, FEATURES> extends
    AbstractGameFactory<IMPL> implements MultiPlayerGameFactory<IMPL, FEATURES> {

  public AbstractMultiPlayerGameFactory(final List<GameInitializer<IMPL>> gameInitializers,
      final List<GameValidator<IMPL>> gameValidators) {
    super(gameInitializers, gameValidators);
  }

  public IMPL createGame(final Set<FEATURES> features, final List<Player> players,
      final Player initiatingPlayer) {
    IMPL game = createFreshGame(features, players, initiatingPlayer);

    prepareGame(game);
    return game;
  }

  @Override
  protected void copyFromPreviousGame(final IMPL previousGame, final IMPL newGame) {
    super.copyFromPreviousGame(previousGame, newGame);
  }

  public IMPL createGame(final IMPL previousGame, final Player initiatingPlayer) {
    List<Player> players = rotatePlayers(previousGame);
    IMPL game = (IMPL) createFreshGame(previousGame.getFeatures(), players, initiatingPlayer);
    copyFromPreviousGame(previousGame, game);
    prepareGame(game);
    return game;
  }

  @SuppressWarnings("WeakerAccess")
  protected List<Player> rotatePlayers(final IMPL previousGame) {
    List<Player> players = new ArrayList<Player>();
    players.addAll(previousGame.getPlayers());
    players.add(players.remove(0));
    return players;
  }

  @SuppressWarnings("WeakerAccess")
  protected IMPL createFreshGame(final Set<FEATURES> features, final List<Player> players,
      final Player initiatingPlayer) {
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
