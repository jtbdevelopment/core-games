package com.jtbdevelopment.games.tracking;

import com.jtbdevelopment.games.players.Player;

/**
 * Date: 3/28/15 Time: 2:42 PM
 */
public class PlayerGameEligibilityResult {

  private Player player;
  private PlayerGameEligibility eligibility;

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public PlayerGameEligibility getEligibility() {
    return eligibility;
  }

  public void setEligibility(PlayerGameEligibility eligibility) {
    this.eligibility = eligibility;
  }
}
