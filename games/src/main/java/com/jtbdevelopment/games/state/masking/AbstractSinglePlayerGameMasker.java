package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import java.io.Serializable;
import java.util.Map;

/**
 * Date: 2/19/15 Time: 7:20 AM
 */
public abstract class AbstractSinglePlayerGameMasker<
    ID extends Serializable, FEATURES,
    U extends AbstractSinglePlayerGame<ID, FEATURES>,
    M extends AbstractMaskedSinglePlayerGame<FEATURES>>
    extends AbstractGameMasker<ID, FEATURES, U, M> implements GameMasker<ID, U, M> {

  @Override
  protected void copyMaskedData(final U game, final Player<ID> player, final M playerMaskedGame,
      final Map<ID, Player<ID>> idMap) {
    super.copyMaskedData(game, player, playerMaskedGame, idMap);
    playerMaskedGame.getPlayers().put(player.getMd5(), player.getDisplayName());
    playerMaskedGame.getPlayerImages().put(player.getMd5(), player.getImageUrl());
    playerMaskedGame.getPlayerProfiles().put(player.getMd5(), player.getProfileUrl());
  }

  @Override
  protected void copyUnmaskedData(final U game, final M playerMaskedGame) {
    super.copyUnmaskedData(game, playerMaskedGame);
  }


}
