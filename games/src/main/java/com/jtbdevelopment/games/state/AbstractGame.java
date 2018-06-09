package com.jtbdevelopment.games.state;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 * Date: 12/31/2014 Time: 5:25 PM
 */
public abstract class AbstractGame<ID extends Serializable, FEATURES>
    implements Game<ID, Instant, FEATURES>, Serializable {

  @Version
  private Integer version;
  @CreatedDate
  private Instant created;
  @LastModifiedDate
  private Instant lastUpdate;
  private int round;
  private Instant completedTimestamp;
  private GamePhase gamePhase = GamePhase.Setup;
  private Set<FEATURES> features = new HashSet<>();

  public boolean equals(final Object o) {
    if (!(o instanceof Game)) {
      return false;
    }

    final Game game = (Game) o;

    return getId().equals(game.getId());

  }

  public int hashCode() {
    String idAsString = getIdAsString();
    return idAsString == null ? 0 : idAsString.hashCode();
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Instant lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public int getRound() {
    return round;
  }

  public void setRound(int round) {
    this.round = round;
  }

  public Instant getCompletedTimestamp() {
    return completedTimestamp;
  }

  public void setCompletedTimestamp(Instant completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
  }

  public GamePhase getGamePhase() {
    return gamePhase;
  }

  public void setGamePhase(GamePhase gamePhase) {
    this.gamePhase = gamePhase;
  }

  public Set<FEATURES> getFeatures() {
    return features;
  }

  public void setFeatures(Set<FEATURES> features) {
    this.features = features;
  }

}
