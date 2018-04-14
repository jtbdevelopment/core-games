package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 1/7/15 Time: 6:38 AM
 */
public abstract class AbstractSinglePlayerGame<ID extends Serializable, FEATURES> extends
    AbstractGame<ID, FEATURES> implements SinglePlayerGame<ID, Instant, FEATURES> {

  private Player<ID> player;

  public List<Player<ID>> getAllPlayers() {
    return new ArrayList<>(Collections.singletonList(player));
  }

  public Player<ID> getPlayer() {
    return player;
  }

  public void setPlayer(Player<ID> player) {
    this.player = player;
  }
}
