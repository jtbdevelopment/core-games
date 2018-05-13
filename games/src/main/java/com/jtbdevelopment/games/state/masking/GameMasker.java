package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import java.io.Serializable;

/**
 * Date: 2/19/15 Time: 7:14 AM
 */
public interface GameMasker<ID extends Serializable, U extends Game, M extends MaskedGame> {

  M maskGameForPlayer(final U game, final Player<ID> player);
}
