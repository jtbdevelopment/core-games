package com.jtbdevelopment.games.factory.gameinitializers;

import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.PlayerState;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/14 Time: 7:01 AM
 */
@Component
public class PlayerStateInitializer<ID extends Serializable, FEATURES, IMPL extends AbstractGame<ID, FEATURES>> implements
    GameInitializer<IMPL> {

  @Override
  public void initializeGame(final AbstractGame game) {
    if (game instanceof AbstractMultiPlayerGame) {
      //noinspection unchecked
      final AbstractMultiPlayerGame<ID, FEATURES> multiPlayerGame = (AbstractMultiPlayerGame<ID, FEATURES>) game;
      final Map<ID, PlayerState> playerStates = multiPlayerGame.getPlayerStates();
      List<Player<ID>> players = multiPlayerGame.getPlayers();
      players.forEach(p -> playerStates.put(p.getId(), PlayerState.Pending));
      playerStates.put(multiPlayerGame.getInitiatingPlayer(), PlayerState.Accepted);

    }
  }

  public final int getOrder() {
    return DEFAULT_ORDER;
  }
}
