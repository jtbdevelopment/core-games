package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * Date: 2/19/15 Time: 7:20 AM
 */
public abstract class AbstractMultiPlayerGameMasker<ID extends Serializable, FEATURES, U extends MultiPlayerGame<ID, Instant, FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>> extends
    AbstractGameMasker<ID, FEATURES, U, M> implements GameMasker<ID, U, M> {

  @Override
  protected void copyMaskedData(final U game, final Player<ID> player, final M playerMaskedGame,
      final Map<ID, Player<ID>> idMap) {
    super.copyMaskedData(game, player, playerMaskedGame, idMap);
    playerMaskedGame.setMaskedForPlayerMD5(player.getMd5());
    playerMaskedGame.setMaskedForPlayerID(player.getIdAsString());
    game.getAllPlayers().forEach(p -> {
      playerMaskedGame.getPlayers().put(p.getMd5(), p.getDisplayName());
      playerMaskedGame.getPlayerImages().put(p.getMd5(), p.getImageUrl());
      playerMaskedGame.getPlayerProfiles().put(p.getMd5(), p.getProfileUrl());
      playerMaskedGame.getPlayerStates().put(p.getMd5(), game.getPlayerStates().get(p.getId()));
    });
    playerMaskedGame.setInitiatingPlayer(idMap.get(game.getInitiatingPlayer()).getMd5());
  }

  @Override
  protected void copyUnmaskedData(final U game, final M playerMaskedGame) {
    super.copyUnmaskedData(game, playerMaskedGame);
    playerMaskedGame.setDeclinedTimestamp(convertTime(game.getDeclinedTimestamp()));
    playerMaskedGame.setRematchTimestamp(convertTime(game.getRematchTimestamp()));
  }
}
