package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;
import java.beans.Transient;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Date: 12/31/2014 Time: 4:56 PM
 */
public interface Game<ID extends Serializable, TIMESTAMP, FEATURES> {

  ID getId();

  void setId(final ID id);

  @Transient
  String getIdAsString();

  Integer getVersion();

  void setVersion(final Integer version);

  ID getPreviousId();

  void setPreviousId(final ID previousID);

  @Transient
  String getPreviousIdAsString();

  int getRound();

  void setRound(final int round);

  TIMESTAMP getCreated();

  void setCreated(final TIMESTAMP created);

  TIMESTAMP getLastUpdate();

  void setLastUpdate(final TIMESTAMP lastUpdate);

  TIMESTAMP getCompletedTimestamp();

  void setCompletedTimestamp(final TIMESTAMP completed);

  Set<FEATURES> getFeatures();

  void setFeatures(final Set<FEATURES> features);

  GamePhase getGamePhase();

  void setGamePhase(final GamePhase gamePhase);

  List<Player<ID>> getAllPlayers();
}
