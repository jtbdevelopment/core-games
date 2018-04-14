package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 12/31/2014 Time: 5:30 PM
 */
public abstract class AbstractMultiPlayerGame<ID extends Serializable, FEATURES> extends
    AbstractGame<ID, FEATURES> implements MultiPlayerGame<ID, Instant, FEATURES> {

  private ID initiatingPlayer;
  private List<Player<ID>> players = new ArrayList<>();
  private Map<ID, PlayerState> playerStates = new HashMap<>();
  private Instant declinedTimestamp;
  private Instant rematchTimestamp;

  public List<Player<ID>> getAllPlayers() {
    return players;
  }

  public ID getInitiatingPlayer() {
    return initiatingPlayer;
  }

  public void setInitiatingPlayer(ID initiatingPlayer) {
    this.initiatingPlayer = initiatingPlayer;
  }

  public List<Player<ID>> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player<ID>> players) {
    this.players = players;
  }

  public Map<ID, PlayerState> getPlayerStates() {
    return playerStates;
  }

  public void setPlayerStates(Map<ID, PlayerState> playerStates) {
    this.playerStates = playerStates;
  }

  public Instant getDeclinedTimestamp() {
    return declinedTimestamp;
  }

  public void setDeclinedTimestamp(Instant declinedTimestamp) {
    this.declinedTimestamp = declinedTimestamp;
  }

  public Instant getRematchTimestamp() {
    return rematchTimestamp;
  }

  public void setRematchTimestamp(Instant rematchTimestamp) {
    this.rematchTimestamp = rematchTimestamp;
  }
}
