package com.jtbdevelopment.games.publish.cluster;

import java.io.Serializable;

/**
 * Date: 3/3/15 Time: 6:57 PM
 */
public class ClusterMessage implements Serializable {

  private ClusterMessageType clusterMessageType;
  private String gameId;
  private String playerId;

  @SuppressWarnings("WeakerAccess")
  public ClusterMessageType getClusterMessageType() {
    return clusterMessageType;
  }

  public void setClusterMessageType(ClusterMessageType clusterMessageType) {
    this.clusterMessageType = clusterMessageType;
  }

  @SuppressWarnings("WeakerAccess")
  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  @SuppressWarnings("WeakerAccess")
  public String getPlayerId() {
    return playerId;
  }

  public void setPlayerId(String playerId) {
    this.playerId = playerId;
  }

  public enum ClusterMessageType {
    GameUpdate, PlayerUpdate, AllPlayersUpdate
  }
}
