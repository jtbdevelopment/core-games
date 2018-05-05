package com.jtbdevelopment.games.state;

import java.beans.Transient;

/**
 * Date: 11/14/14 Time: 12:34 PM
 */
public enum GamePhase {
  Playing("Game in play!", "Play"),
  Challenged("Challenge delivered.", "Challenged"),
  Setup("Game setup in progress.", "Setup"),
  RoundOver("Round finished.", "Played", 7),
  NextRoundStarted("Next round begun.", "Finished", 7),
  Declined("Challenge declined.", "Declined", 7),
  Quit("Game quit.", "Quit", 7);

  private final String description;
  private final String groupLabel;
  private final int historyCutoffDays;

  GamePhase(final String description, final String groupLabel, int historyCutoffDays) {
    this.description = description;
    this.groupLabel = groupLabel;
    this.historyCutoffDays = historyCutoffDays;
  }

  GamePhase(final String description, final String groupLabel) {
    this(description, groupLabel, 30);
  }

  @Transient
  public String getDescription() {
    return description;
  }

  @Transient
  public String getGroupLabel() {
    return groupLabel;
  }

  @Transient
  public int getHistoryCutoffDays() {
    return historyCutoffDays;
  }

}
