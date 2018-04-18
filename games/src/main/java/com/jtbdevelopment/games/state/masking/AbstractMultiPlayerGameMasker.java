package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Date: 2/19/15
 * Time: 7:20 AM
 */
public abstract class AbstractMultiPlayerGameMasker<ID extends Serializable, FEATURES, U extends MultiPlayerGame<ID, Instant, FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>> extends
    AbstractGameMasker<ID, FEATURES, U, M> implements GameMasker<ID, U, M> {

  @Override
  protected void copyMaskedData(final U game, final Player<ID> player, final M playerMaskedGame,
      final Map<ID, Player<ID>> idMap) {
    super.copyMaskedData(game, player, playerMaskedGame, idMap);
    playerMaskedGame.setMaskedForPlayerMD5(player.getMd5());
    playerMaskedGame.setMaskedForPlayerID(player.getIdAsString());
    playerMaskedGame.setPlayers(game.getAllPlayers().stream().collect(Collectors.toMap(
        Player::getMd5, Player::getDisplayName)));
    playerMaskedGame.setPlayerImages(game.getAllPlayers().stream().collect(Collectors.toMap(
        Player::getMd5, Player::getImageUrl)));
    playerMaskedGame.setPlayerProfiles(game.getAllPlayers().stream().collect(Collectors.toMap(
        Player::getMd5, Player::getProfileUrl)));
    playerMaskedGame.setInitiatingPlayer(idMap.get(game.getInitiatingPlayer()).getMd5());
    playerMaskedGame.setPlayerStates(game.getAllPlayers().stream().collect(Collectors.toMap(
        Player::getMd5, p -> game.getPlayerStates().get(p.getId()))));
  }

  @Override
  protected void copyUnmaskedData(final U game, final M playerMaskedGame) {
    super.copyUnmaskedData(game, playerMaskedGame);
    playerMaskedGame.setDeclinedTimestamp(convertTime(game.getDeclinedTimestamp()));
    playerMaskedGame.setRematchTimestamp(convertTime(game.getRematchTimestamp()));
  }
}
