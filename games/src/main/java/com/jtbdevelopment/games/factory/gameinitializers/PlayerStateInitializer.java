package com.jtbdevelopment.games.factory.gameinitializers;

import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.PlayerState;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/14 Time: 7:01 AM
 */
@Component
public class PlayerStateInitializer implements GameInitializer<Game> {

  @Override
  public void initializeGame(final Game game) {
    if (game instanceof MultiPlayerGame) {
      final MultiPlayerGame multiPlayerGame = (MultiPlayerGame) game;
      final Map<Serializable, PlayerState> playerStates = multiPlayerGame.getPlayerStates();
      List<Player> players = multiPlayerGame.getPlayers();
      players.forEach(p -> playerStates.put(p.getId(), PlayerState.Pending));
      playerStates.put(multiPlayerGame.getInitiatingPlayer(), PlayerState.Accepted);

    }
  }

  public final int getOrder() {
    return DEFAULT_ORDER;
  }
}
