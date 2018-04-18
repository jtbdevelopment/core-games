package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import java.beans.Transient;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
public abstract class AbstractMaskedGame<FEATURES> implements MaskedGame<FEATURES> {

  private String id;
  private String previousId;
  private Integer version;
  private int round;
  private Long created;
  private Long lastUpdate;
  private Long completedTimestamp;
  private GamePhase gamePhase;
  private Map<String, String> players = new LinkedHashMap<>();
  private Map<String, String> playerImages = new LinkedHashMap<>();
  private Map<String, String> playerProfiles = new LinkedHashMap<>();
  private Set<FEATURES> features = new HashSet<>();
  private Map<FEATURES, Object> featureData = new LinkedHashMap<>();

  @Override
  @Transient
  public String getIdAsString() {
    return id;
  }

  public void setIdAsString(@SuppressWarnings("unused") final String id) {
  }

  @Override
  @Transient
  public String getPreviousIdAsString() {
    return previousId;
  }

  @Override
  public List<Player<String>> getAllPlayers() {
    return null;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPreviousId() {
    return previousId;
  }

  public void setPreviousId(String previousId) {
    this.previousId = previousId;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public int getRound() {
    return round;
  }

  public void setRound(int round) {
    this.round = round;
  }

  public Long getCreated() {
    return created;
  }

  public void setCreated(Long created) {
    this.created = created;
  }

  public Long getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public Long getCompletedTimestamp() {
    return completedTimestamp;
  }

  public void setCompletedTimestamp(Long completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
  }

  public GamePhase getGamePhase() {
    return gamePhase;
  }

  public void setGamePhase(GamePhase gamePhase) {
    this.gamePhase = gamePhase;
  }

  public Map<String, String> getPlayers() {
    return players;
  }

  public void setPlayers(Map<String, String> players) {
    this.players = players;
  }

  public Map<String, String> getPlayerImages() {
    return playerImages;
  }

  public void setPlayerImages(Map<String, String> playerImages) {
    this.playerImages = playerImages;
  }

  public Map<String, String> getPlayerProfiles() {
    return playerProfiles;
  }

  public void setPlayerProfiles(Map<String, String> playerProfiles) {
    this.playerProfiles = playerProfiles;
  }

  public Set<FEATURES> getFeatures() {
    return features;
  }

  public void setFeatures(Set<FEATURES> features) {
    this.features = features;
  }

  public Map<FEATURES, Object> getFeatureData() {
    return featureData;
  }

  public void setFeatureData(Map<FEATURES, Object> featureData) {
    this.featureData = featureData;
  }
}
