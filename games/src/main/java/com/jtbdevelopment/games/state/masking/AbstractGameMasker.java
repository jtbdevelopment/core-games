package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Date: 2/19/15 Time: 7:20 AM
 */
public abstract class AbstractGameMasker<ID extends Serializable, FEATURES, U extends Game<ID, Instant, FEATURES>, M extends MaskedGame<FEATURES>> implements
    GameMasker<ID, U, M> {

  protected abstract M newMaskedGame();

  protected abstract Class<ID> getIDClass();

  @Override
  public M maskGameForPlayer(final U game, final Player<ID> player) {
    M playerMaskedGame = newMaskedGame();

    copyUnmaskedData(game, playerMaskedGame);

    Map<ID, Player<ID>> idMap = createIDMap(game);
    copyMaskedData(game, player, playerMaskedGame, idMap);

    return playerMaskedGame;
  }

  protected Map<ID, Player<ID>> createIDMap(final U game) {
    return game.getAllPlayers().stream().collect(Collectors.toMap(Player::getId, p -> p));
  }

  protected void copyMaskedData(final U game, final Player<ID> player, final M playerMaskedGame,
      final Map<ID, Player<ID>> idMap) {
    final Class<ID> idClass = getIDClass();
    playerMaskedGame
        .setFeatureData(game.getFeatureData().entrySet().stream().collect(Collectors.toMap(
            Entry::getKey,
            entry -> idClass.isInstance(entry.getValue()) ? idMap.get(entry.getValue())
                : entry.getValue()
        )));
  }

  protected void copyUnmaskedData(final U game, final M playerMaskedGame) {
    playerMaskedGame.setCompletedTimestamp(convertTime(game.getCompletedTimestamp()));
    playerMaskedGame.setCreated(convertTime(game.getCreated()));
    playerMaskedGame.setLastUpdate(convertTime(game.getLastUpdate()));
    playerMaskedGame.getFeatures().addAll(game.getFeatures());
    playerMaskedGame.setId(game.getIdAsString());
    playerMaskedGame.setGamePhase(game.getGamePhase());
    playerMaskedGame.setRound(game.getRound());
    playerMaskedGame.setPreviousId(game.getPreviousIdAsString());
  }

  @SuppressWarnings("WeakerAccess")
  protected Long convertTime(final Instant value) {
    return value != null ? value.toEpochMilli() : null;
  }
}
