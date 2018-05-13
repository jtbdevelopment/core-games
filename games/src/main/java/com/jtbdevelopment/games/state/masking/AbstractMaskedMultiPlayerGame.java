package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.state.PlayerState;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2/18/15 Time: 6:55 PM
 */
public abstract class AbstractMaskedMultiPlayerGame<FEATURES> extends
    AbstractMaskedGame<FEATURES> implements MaskedMultiPlayerGame<FEATURES> {

  private String maskedForPlayerID;
  private String maskedForPlayerMD5;
  private Long declinedTimestamp;
  private Long rematchTimestamp;
  private String initiatingPlayer;
  private Map<String, PlayerState> playerStates = new HashMap<>();

  public String getMaskedForPlayerID() {
    return maskedForPlayerID;
  }

  public void setMaskedForPlayerID(String maskedForPlayerID) {
    this.maskedForPlayerID = maskedForPlayerID;
  }

  public String getMaskedForPlayerMD5() {
    return maskedForPlayerMD5;
  }

  public void setMaskedForPlayerMD5(String maskedForPlayerMD5) {
    this.maskedForPlayerMD5 = maskedForPlayerMD5;
  }

  public Long getDeclinedTimestamp() {
    return declinedTimestamp;
  }

  public void setDeclinedTimestamp(Long declinedTimestamp) {
    this.declinedTimestamp = declinedTimestamp;
  }

  public Long getRematchTimestamp() {
    return rematchTimestamp;
  }

  public void setRematchTimestamp(Long rematchTimestamp) {
    this.rematchTimestamp = rematchTimestamp;
  }

  public String getInitiatingPlayer() {
    return initiatingPlayer;
  }

  public void setInitiatingPlayer(String initiatingPlayer) {
    this.initiatingPlayer = initiatingPlayer;
  }

  public Map<String, PlayerState> getPlayerStates() {
    return playerStates;
  }

  public void setPlayerStates(Map<String, PlayerState> playerStates) {
    this.playerStates = playerStates;
  }
}
